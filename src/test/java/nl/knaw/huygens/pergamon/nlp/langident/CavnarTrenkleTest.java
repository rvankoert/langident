package nl.knaw.huygens.pergamon.nlp.langident;

import org.junit.Test;

import java.io.IOException;

public class CavnarTrenkleTest extends LanguageGuesserTest {
  @Test
  public void test() throws IOException, ClassNotFoundException {
    test(new CavnarTrenkle());
  }
}
