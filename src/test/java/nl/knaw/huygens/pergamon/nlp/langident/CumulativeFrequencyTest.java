package nl.knaw.huygens.pergamon.nlp.langident;

import org.junit.Test;

public class CumulativeFrequencyTest extends LanguageGuesserTest {
  @Test
  public void testCumulativeFrequency() {
    test(new CumulativeFrequency());
  }
}
