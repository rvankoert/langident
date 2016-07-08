package nl.knaw.huygens.pergamon.nlp;

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
