package nl.knaw.huygens.pergamon.nlp.langident;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class NaiveBayesTest extends LanguageGuesserTest {
  @Test
  public void test() throws IOException, ClassNotFoundException {
    LanguageGuesser nb = new NaiveBayes();
    test(nb);

    List<LanguageGuesser.Prediction> pred = nb.predictScores("zomaar een testje");
    double total = pred.stream().mapToDouble(LanguageGuesser.Prediction::getScore).sum();
    Assert.assertTrue(Math.abs(total - 1) < 1e-14);
  }
}
