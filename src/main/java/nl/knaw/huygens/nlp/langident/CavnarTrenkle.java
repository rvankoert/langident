package nl.knaw.huygens.nlp.langident;

import nl.knaw.huygens.util.Collections2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Cavnar-Trenkle style (n-gram frequency ranking) language guesser.
 * <p>
 * After W.B. Cavnar & J.M. Trenkle, N-gram-based text categorization, SDAIR-1994,
 * http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.53.9367
 */
public class CavnarTrenkle extends NGramFeaturesClassifier {
    // Keep only this many n-grams for classification.
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

    public void train(List<CharSequence> docs, List<String> labels) {
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
    }

    // Convert frequency table to list of the <cutoff> most frequent items, in sorted order.
    private CharSequence[] freqsToRanks(Map<CharSequence, Long> freqs) {
        List<Map.Entry<CharSequence, Long>> entries = freqs.entrySet().stream().collect(Collectors.toList());

        Collections2.partialSort(entries, (e1, e2) -> Long.compare(e2.getValue(), e1.getValue()), cutoff);

        return entries.subList(0, Math.min(cutoff, entries.size())).stream()
                .map(entry -> entry.getKey())
                .toArray(CharSequence[]::new);
    }

    public String predict(CharSequence doc) {
        CharSequence[] sorted = freqsToRanks(features(doc).collect(Collectors.groupingBy(Function.identity(),
                Collectors.counting())));

        List<String> labels = rankPerLabel.keySet().stream().collect(Collectors.toList());
        long[] distances = IntStream.range(0, labels.size()).parallel().mapToLong(i ->
                distance(sorted, labels.get(i))).toArray();
        return labels.get(IntStream.range(0, labels.size()).boxed()
                .collect(Collectors.minBy((i, j) -> Long.compare(distances[i], distances[j])))
                .get());
    }

    // Cavnar-Trenkle "out of place distance" between two ranked lists.
    private long distance(CharSequence[] profile, String label) {
        Map<CharSequence, Integer> labelProfile = rankPerLabel.get(label);
        return IntStream.range(0, profile.length).parallel().mapToLong(rank -> {
            CharSequence ngram = profile[rank];
            return (long)labelProfile.getOrDefault(ngram, cutoff);
        }).sum();
    }
}