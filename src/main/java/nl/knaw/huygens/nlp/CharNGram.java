package nl.knaw.huygens.nlp;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Contains static methods for generating character n-grams.
 */
public class CharNGram {
    /**
     * Generates character n-grams of s, of lengths in the range [minN, maxN]
     * (inclusive).
     * <p>
     * Returns a sequential stream of n-grams, represented as CharSequences.
     *
     * @param minN Minimal length of n-grams.
     * @param maxN Maximal length of n-grams.
     * @param s
     * @return
     */
    public static Stream<CharSequence> generate(CharSequence s, int minN, int maxN) {
        int len = s.length();
        return IntStream.range(0, len - minN + 1).boxed()
                .flatMap(index -> IntStream.range(minN, maxN + 1)
                        .mapToObj(n -> (index + n <= len) ? s.subSequence(index, index + n) : null)
                        .filter(ngram -> ngram != null));
    }

    /**
     * Equivalent to generate(s, n, n).
     *
     * @param s
     * @param n
     * @return
     */
    public static Stream<CharSequence> generate(CharSequence s, int n) {
        return generate(s, n, n);
    }
}