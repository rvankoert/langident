package nl.knaw.huygens.pergamon.nlp.langident;

import java.util.List;
import java.util.Set;

public interface Model {
  /**
   * Prediction of the language of a document.
   */
  class Prediction {
    private final String label;
    private final double score;

    Prediction(String label, double score) {
      this.label = label;
      this.score = score;
    }

    public String getLabel() {
      return label;
    }

    /**
     * The score of this Prediction's language wrt. the input.
     * <p>
     * The interpretation of these scores is algorithm-specific; they may
     * be probabilities, similarity scores or inverse distances.
     */
    public double getScore() {
      return score;
    }
  }

  /**
   * @return The Trainer that produced this model.
   */
  Trainer getTrainer();

  /**
   * The set of languages known to this language guesser.
   *
   * @return An immutable Set of language codes.
   */
  Set<String> languages();

  /**
   * Predict the language of the document doc.
   *
   * @param doc Any piece of text.
   * @return The code for the highest-scoring language assigned to doc.
   */
  default String predictBest(CharSequence doc) {
    return predictScores(doc).get(0).getLabel();
  }

  /**
   * Predict the language of the document doc.
   *
   * @param doc A piece of text.
   * @return A list of predictions, sorted from highest-scoring to lowest.
   */
  List<Prediction> predictScores(CharSequence doc);
}
