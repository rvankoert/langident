package nl.knaw.huygens.pergamon.nlp.langident;

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
