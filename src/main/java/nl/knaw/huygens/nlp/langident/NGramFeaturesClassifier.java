package nl.knaw.huygens.nlp.langident;

import nl.knaw.huygens.nlp.CharNGram;

import java.util.stream.Stream;

/**
 * Abstract base class for classifiers based on character n-gram features.
 */
public abstract class NGramFeaturesClassifier implements Classifier {
    // Sizes of n-grams.
    protected int minN = 2, maxN = 7;

    // Feature extraction.
    protected Stream<CharSequence> features(CharSequence doc) {
        return CharNGram.generate(doc.toString().toLowerCase(), minN, maxN);
    }
}
