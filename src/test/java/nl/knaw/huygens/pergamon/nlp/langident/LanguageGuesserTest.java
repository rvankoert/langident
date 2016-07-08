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

import java.util.ArrayList;
import java.util.List;

// Base class for language guesser tests. Actual test cases should call the test()
// method on a specific LanguageGuesser instance.
public class LanguageGuesserTest {
  private static String[] english = new String[]{
    "IN Xanadu did Kubla Khan",
    "A stately pleasure-dome decree:",
    "Where Alph, the sacred river, ran",
    "Through caverns measureless to man",
    "Down to a sunless sea.",
  };

  private static String[] dutch = new String[]{ //
    "Het hemelsche gerecht heeft zich ten langen leste",
    "Erbarremt over my en myn benauwde veste,",
    "En arme burgery, en op myn volcx gebed,",
    "En dagelix geschrey de bange stad ontzet."
  };

  private static String[] italian = new String[]{
    "Nel mezzo del cammin di nostra vita",
    "mi ritrovai per una selva oscura,",
    "ché la diritta via era smarrita.",
    "Ahi quanto a dir qual era è cosa dura",
    "esta selva selvaggia e aspra e forte",
    "che nel pensier rinova la paura!"
  };

  protected void test(LanguageGuesser guesser) {
    List<CharSequence> samples = new ArrayList<CharSequence>();
    List<String> labels = new ArrayList<String>();

    for (String sample : english) {
      samples.add(sample);
      labels.add("en");
    }
    for (String sample : dutch) {
      samples.add(sample);
      labels.add("nl");
    }
    for (String sample : italian) {
      samples.add(sample);
      labels.add("it");
    }

    guesser = guesser.train(samples, labels);

    Assert.assertEquals("it", guesser.predictBest("lasciate ogni speranza, voi ch'intrate"));
    Assert.assertEquals("en", guesser.predictBest("The end is nigh."));
    Assert.assertEquals("nl", guesser.predictBest("scheveningen"));

    String[] langs = guesser.languages().stream().sorted().toArray(String[]::new);
    Assert.assertArrayEquals(new String[]{"en", "it", "nl"}, langs);

    List<LanguageGuesser.Prediction> pred = guesser.predictScores("Hallo, wereld!");
    Assert.assertArrayEquals(langs,
      pred.stream().map(LanguageGuesser.Prediction::getLang).sorted().toArray());

    // We want the languages by descending order of scores.
    for (int i = 1; i < pred.size(); i++) {
      Assert.assertTrue(pred.get(i - 1).getScore() >= pred.get(i).getScore());
    }
  }
}
