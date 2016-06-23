package nl.knaw.huygens.nlp.langident;

import org.junit.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LanguageGuesserTest {
  private static String[] latin = new String[]{
    "Quod licet Iovi, non licet bovi.",
    "Ut desint vires, tamen laudanda est voluntas.",
    "Quis deus incertum est."
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

  public void test(Classifier classifier) throws ClassNotFoundException, IOException {
    List<CharSequence> samples = new ArrayList<CharSequence>();
    List<String> labels = new ArrayList<String>();

    for (String sample : latin) {
      samples.add(sample);
      labels.add("la");
    }
    for (String sample : dutch) {
      samples.add(sample);
      labels.add("nl");
    }
    for (String sample : italian) {
      samples.add(sample);
      labels.add("it");
    }

    classifier.train(samples, labels);
    simpleExamples(classifier);

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ObjectOutputStream str = new ObjectOutputStream(bytes);
    str.writeObject(classifier);
    classifier = (Classifier) new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();

    simpleExamples(classifier);
  }

  private void simpleExamples(Classifier classifier) {
    Assert.assertEquals("it", classifier.predict("lasciate ogni speranza, voi ch'intrate"));
    Assert.assertEquals("la", classifier.predict("Ut desint viri, tamen laudanda est voluptas."));
    Assert.assertEquals("nl", classifier.predict("scheveningen"));
  }

}
