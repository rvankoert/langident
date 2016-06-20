package nl.knaw.huygens.nlp.langident;

import org.junit.Test;

import java.io.IOException;

public class NaiveBayesTest extends LanguageGuesserTest {
    @Test
    public void test() throws IOException, ClassNotFoundException {
        test(new NaiveBayes());
    }
}
