package nl.knaw.huygens.pergamon.nlp.langident;

/*-
 * #%L
 * langident
 * %%
 * Copyright (C) 2016 Huygens ING (KNAW)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
     * The interpretation of these scores is algorithm-specific; they may be probabilities, similarity scores or
     * inverse distances.
     * </p>
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
