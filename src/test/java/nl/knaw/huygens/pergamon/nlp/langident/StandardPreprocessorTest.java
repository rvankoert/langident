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

import org.junit.Assert;
import org.junit.Test;

public class StandardPreprocessorTest {

  private void testPreprocessing(String expected, String input) {
    Assert.assertEquals("<" + input + ">", expected, StandardPreprocessor.preprocess(input));
  }

  @Test
  public void testPreprocessRomanNumerals() {
    // within text
    testPreprocessing(" aa bb ", "AA XIV BB");
    // start of text
    testPreprocessing(" bb ", "XIV BB");
    // end of text
    testPreprocessing(" aa ", "AA XIV");
  }

  @Test
  public void testPreprocessPunctuation() {
    testPreprocessing(" xx xx xx xx xx xx xx xx xx ", "xx,xx.xx:xx;xx!xx?xx&xx/xx");
  }

  @Test
  public void testPreprocessDigits() {
    testPreprocessing(" d d d d ", "d1d123d0d");
  }

  @Test
  public void testPreprocessQuotes() {
    testPreprocessing(" a b c d ", "‘a’ \"b\" “c” «d»");
  }

  @Test
  public void testPreprocessParenthesis() {
    testPreprocessing(" a b c def ", "((a) b) c d(e)f");
  }

  @Test
  public void testPreprocessBrackets() {
    testPreprocessing(" a b c def ", "[[a] b] c d[e]f");
  }

  @Test
  public void testPreprocessDashes() {
    testPreprocessing(" x x-x x ", "x- x-x -x");
  }

  @Test
  public void testPreprocessInitials() {
    testPreprocessing(" signed ", "Signed H.v.M.");
    testPreprocessing(" leeuwenhoek ", "A: Leeuwenhoek");
  }

  @Test
  public void testPreprocessMath() {
    testPreprocessing(" kx a p a ¾q z a b c ", "Kx = ½ a/p2 + ½a + ¾q - √z2/a + b*c");
  }

}
