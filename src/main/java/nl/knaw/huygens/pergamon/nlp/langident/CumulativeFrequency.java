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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements the "Cumulative Frequency Addition" method of Bashir Ahmed, Sung-Hyuk Cha, and Charles Tappert
 * (Proceedings of Student/Faculty Research Day, CSIS, Pace University, May 7th, 2004).
 * <p>
 * This is essentially a botched Naive Bayes that adds where it should multiply.
 */
public class CumulativeFrequency extends BaseTrainer {
  private class CFModel extends BaseModel {
    private Map<String, Map<CharSequence, Double>> featureFreq = new HashMap<>();

    @Override
    public Trainer getTrainer() {
      return CumulativeFrequency.this;
    }

    @Override
    public Set<String> languages() {
      return featureFreq.keySet();
    }

    @Override
    protected Stream<Prediction> predictStream(CharSequence doc) {
      Set<String> langs = languages();
      return features(doc).parallel()
        .flatMap(ngram -> langs.parallelStream().map(lang -> {
          double scoreForLang = featureFreq.get(lang).getOrDefault(ngram, 0.);
          return new Prediction(lang, scoreForLang);
        }))
        .collect(Collectors.groupingBy(Prediction::getLabel,
          Collectors.summingDouble(Prediction::getScore)))
        .entrySet().stream().map(entry -> new Prediction(entry.getKey(), entry.getValue()));
    }
  }

  private static final int MIN_FREQ = 3;
  private static final int TOP_K = 400;


  @Override
  protected Model train(List<CharSequence> docs, List<String> labels) {
    CFModel model = new CFModel();

    if (docs.size() != labels.size()) {
      throw new IllegalArgumentException(String.format("%d samples != %d labels", docs.size(), labels.size()));
    }

    for (String label : labels) {
      model.featureFreq.put(label, new HashMap<CharSequence, Double>());
    }

    for (int i = 0; i < docs.size(); i++) {
      Map<CharSequence, Double> fp = model.featureFreq.get(labels.get(i));
      features(docs.get(i))
        .forEach(ngram -> fp.compute(ngram, (ng, oldCount) -> (oldCount == null ? 0 : oldCount) + 1));
    }

    Map<CharSequence, Double> totalCounts = new HashMap<CharSequence, Double>();
    model.featureFreq.forEach((label, counts) -> {
      counts.forEach((ngram, count) -> totalCounts.put(ngram, totalCounts.getOrDefault(ngram, 0.) + count));
    });

    // Normalize counts.
    totalCounts.forEach((ngram, totalCount) -> {
      model.featureFreq.forEach((label, prob) -> {
        double count = prob.getOrDefault(ngram, 0.);
        prob.put(ngram, count / totalCount);
      });
    });

    return model;
  }
}
