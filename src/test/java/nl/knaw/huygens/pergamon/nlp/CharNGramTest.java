package nl.knaw.huygens.pergamon.nlp;

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
