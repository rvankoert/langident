package nl.knaw.huygens.pergamon.nlp.langident;

/*
 * #%L
 * langident
 * %%
 * Copyright (C) 2016 Huygens ING (KNAW)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import nl.knaw.huygens.algomas.ExtMath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Naive Bayes classifier for language identification.
 * <p>
 * This is a multinomial Naive Bayes classifier using n-gram features, for 2 <=
 * n <= 7, and a uniform prior over languages.
 */
public class NaiveBayes extends LanguageGuesser {
  // Laplace/Lidstone smoothing parameter, currently fixed.
  private double pseudocount = 1.;

  // Feature log-probabilities per label.
  // Maps label -> ngram -> log(P(ngram)).
  private Map<String, Map<CharSequence, Double>> featureProb;

  /**
   * Construct untrained classifier with default settings.
   */
  public NaiveBayes() {
  }

  public NaiveBayes(double pseudocount, int minN, int maxN) {
    this.pseudocount = pseudocount;
    this.minN = minN;
    this.maxN = maxN;
  }

  /**
   * Train Naive Bayes model on labeled documents.
   * <p>
   * Each i'th element of docs is expected have the i'th label in labels. If
   * the length of these two arrays doesn't match, an exception is thrown.
   * <p>
   * Calling train a second time retrains the model.
   *
   * @param docs
   * @param labels
   */
  @Override
  protected void train(List<CharSequence> docs, List<String> labels) {
    featureProb = new HashMap<>();

    if (docs.size() != labels.size()) {
      throw new IllegalArgumentException(String.format("%d samples != %d labels", docs.size(), labels.size()));
    }

    for (String label : labels) {
      featureProb.put(label, new HashMap<>());
    }

    for (int i = 0; i < docs.size(); i++) {
      Map<CharSequence, Double> fp = featureProb.get(labels.get(i));
      features(docs.get(i))
        .forEach(ngram -> fp.compute(ngram, (ng, oldCount) -> (oldCount == null ? 0 : oldCount) + 1));
    }

    Map<CharSequence, Double> totalCounts = new HashMap<CharSequence, Double>();
    featureProb.forEach((label, counts) -> {
      counts.forEach((ngram, count) -> totalCounts.put(ngram, totalCounts.getOrDefault(ngram, 0.) + count));
    });

    // Normalize counts to probabilities; also Lidstone smoothing.
    totalCounts.forEach((ngram, totalCount) -> {
      featureProb.forEach((label, prob) -> {
        double count = prob.getOrDefault(ngram, 0.);
        prob.put(ngram,
          Math.log(count + pseudocount) - Math.log(totalCount + pseudocount * totalCounts.size()));
      });
    });
  }

  @Override
  public Set<String> languages() {
    return featureProb.keySet();
  }

  protected Stream<Prediction> predictStream(CharSequence doc) {
    Map<String, Double> prob = new ConcurrentHashMap<>();

    Set<String> labels = languages();
    final double prior = -Math.log(labels.size());    // Uniform prior.
    for (String label : labels) {
      prob.put(label, prior);
    }

    features(doc).forEach(ngram -> {
      labels.forEach(label -> {
        prob.put(label, prob.get(label) + featureProb.get(label).getOrDefault(ngram, 0.));
      });
    });

    double logTotal = prob.entrySet().stream().mapToDouble(Map.Entry::getValue)
      .reduce(ExtMath::logAddExp).getAsDouble();
    return prob.entrySet().stream().map(entry ->
      new Prediction(entry.getKey(), Math.exp(entry.getValue() - logTotal)));
  }
}
