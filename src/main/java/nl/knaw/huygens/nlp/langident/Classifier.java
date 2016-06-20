package nl.knaw.huygens.nlp.langident;

import java.io.Serializable;
import java.util.List;

/**
 * A trainable language identifier.
 */
public interface Classifier extends Serializable {
    void train(List<CharSequence> docs, List<String> labels);

    String predict(CharSequence doc);
}
