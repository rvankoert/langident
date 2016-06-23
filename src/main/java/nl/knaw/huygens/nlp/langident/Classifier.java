package nl.knaw.huygens.nlp.langident;

import java.util.List;

/**
 * A trainable language identifier.
 */
public interface Classifier {
  /**
   * Train classifier.
   *
   * @param docs
   * @param labels
   * @return this
   */
  Classifier train(List<CharSequence> docs, List<String> labels);

  String predict(CharSequence doc);
}
