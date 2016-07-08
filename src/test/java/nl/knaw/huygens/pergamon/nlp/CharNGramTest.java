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

import org.junit.Assert;
import org.junit.Test;

public class CharNGramTest {
  @Test
  public void simple() {
    // List<CharSequence> ngrams = ngramList(1, 1, "hello");
    Object[] unigrams = CharNGram.generate("hello", 1).toArray();
    Assert.assertArrayEquals(new Object[]{"h", "e", "l", "l", "o"}, unigrams);

    String msg = "héllo, wörld";
    CharNGram.generate(msg, 2, 100).forEach(ngram -> {
      Assert.assertTrue(ngram.length() >= 2);
      Assert.assertTrue(ngram.length() <= msg.length());
    });

    Assert.assertEquals(CharNGram.generate(msg, 2, msg.length()).count(),
      CharNGram.generate(msg, 2, 1000000).count());
  }
}
