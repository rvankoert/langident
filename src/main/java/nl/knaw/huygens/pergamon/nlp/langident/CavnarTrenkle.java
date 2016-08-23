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

import nl.knaw.huygens.algomas.Sort;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Cavnar-Trenkle style (n-gram frequency ranking) language guesser.
 * <p>
 * After W.B. Cavnar & J.M. Trenkle, N-gram-based text categorization, SDAIR-1994,
 * http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.53.9367
 */
public class CavnarTrenkle extends BaseLanguageGuesser implements Model {
  // Keep only this many n-grams for classification. This should be a training-time option,
  // but is hardcoded for now.
  private int cutoff = 400;

  // N-gram frequency ranks: maps label -> n-gram -> frequency.
  private Map<String, Map<CharSequence, Integer>> rankPerLabel;

  public CavnarTrenkle() {
  }

  public CavnarTrenkle(int cutoff) {
    this.cutoff = cutoff;
  }

  public CavnarTrenkle(int cutoff, int minN, int maxN) {
    this.cutoff = cutoff;
    this.minN = minN;
    this.maxN = maxN;
  }

  protected Model train(List<CharSequence> docs, List<String> labels) {
    Set<String> labelSet = new HashSet<>();

    Map<String, Map<CharSequence, Long>> freqPerLabel = new HashMap<>();
    rankPerLabel = new HashMap<>();

    for (int i = 0; i < docs.size(); i++) {
      String label = labels.get(i);
      labelSet.add(label);
      freqPerLabel.put(label, new HashMap<>());
      rankPerLabel.put(label, new HashMap<>());
    }

    for (int i = 0; i < docs.size(); i++) {
      String label = labels.get(i);
      features(docs.get(i)).forEach(ngram -> {
        Map<CharSequence, Long> freq = freqPerLabel.get(label);
        freq.compute(ngram, (ng, oldCount) -> (oldCount == null ? 0 : oldCount) + 1);
      });
    }

    freqPerLabel.forEach((label, freq) -> {
      CharSequence[] sorted = freqsToRanks(freq);

      Map<CharSequence, Integer> ranks = rankPerLabel.get(label);
      for (int rank = 0; rank < Math.min(cutoff, sorted.length); rank++) {
        ranks.put(sorted[rank], rank);
      }
    });

    return this;
  }

  // Convert frequency table to list of the <cutoff> most frequent items, in sorted order.
  private CharSequence[] freqsToRanks(Map<CharSequence, Long> freqs) {
    List<Map.Entry<CharSequence, Long>> entries = freqs.entrySet().stream().collect(Collectors.toList());

    Sort.partial(entries, (e1, e2) -> Long.compare(e2.getValue(), e1.getValue()), cutoff);

    return entries.subList(0, Math.min(cutoff, entries.size())).stream()
      .map(entry -> entry.getKey())
      .toArray(CharSequence[]::new);
  }

  @Override
  public Set<String> languages() {
    return rankPerLabel.keySet();
  }

  @Override
  protected Stream<Prediction> predictStream(CharSequence doc) {
    CharSequence[] docProfile = freqsToRanks(features(doc).collect(Collectors.groupingBy(Function.identity(),
      Collectors.counting())));

    return rankPerLabel.entrySet().parallelStream()
      .map(entry -> {
        String label = entry.getKey();
        long dist = distance(docProfile, entry.getValue());
        // The predictBest method wants scores with "higher is better", but what
        // we have are distances ("smaller is better"). The following is an
        // arbitrary way of converting these into scores; the +1 prevents zero
        // division.
        double score = 1. / (dist + 1.);

        return new Prediction(label, score);
      });
  }

  // Cavnar-Trenkle "out of place distance" between two ranked lists.
  private long distance(CharSequence[] profile, Map<CharSequence, Integer> labelProfile) {
    return IntStream.range(0, profile.length).parallel().mapToLong(rank -> {
      CharSequence ngram = profile[rank];
      return (long) labelProfile.getOrDefault(ngram, cutoff);
    }).sum();
  }
}
