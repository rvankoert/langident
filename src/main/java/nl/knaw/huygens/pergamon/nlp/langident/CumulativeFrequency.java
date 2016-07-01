package nl.knaw.huygens.pergamon.nlp.langident;

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
public class CumulativeFrequency extends LanguageGuesser {
  private static final int MIN_FREQ = 3;
  private static final int TOP_K = 400;

  private Map<String, Map<CharSequence, Double>> featureFreq;

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
      .collect(Collectors.groupingBy(Prediction::getLang,
        Collectors.summingDouble(Prediction::getScore)))
      .entrySet().stream().map(entry -> new Prediction(entry.getKey(), entry.getValue()));
  }

  @Override
  public LanguageGuesser train(List<CharSequence> docs, List<String> labels) {
    featureFreq = new HashMap<String, Map<CharSequence, Double>>();

    if (docs.size() != labels.size()) {
      throw new IllegalArgumentException(String.format("%d samples != %d labels", docs.size(), labels.size()));
    }

    for (String label : labels) {
      featureFreq.put(label, new HashMap<CharSequence, Double>());
    }

    for (int i = 0; i < docs.size(); i++) {
      Map<CharSequence, Double> fp = featureFreq.get(labels.get(i));
      features(docs.get(i))
        .forEach(ngram -> fp.compute(ngram, (ng, oldCount) -> (oldCount == null ? 0 : oldCount) + 1));
    }

    Map<CharSequence, Double> totalCounts = new HashMap<CharSequence, Double>();
    featureFreq.forEach((label, counts) -> {
      counts.forEach((ngram, count) -> totalCounts.put(ngram, totalCounts.getOrDefault(ngram, 0.) + count));
    });

    // Normalize counts.
    totalCounts.forEach((ngram, totalCount) -> {
      featureFreq.forEach((label, prob) -> {
        double count = prob.getOrDefault(ngram, 0.);
        prob.put(ngram, count / totalCount);
      });
    });

    return this;
  }
}
