package nl.knaw.huygens.pergamon.nlp.langident;

/**
 * A trainer encapsulates a training algorithm with hyperparameters.
 * <p>
 * Training using a Trainer produces a {@link Model}, which can be used to make predictions.
 * </p>
 */
public interface Trainer {
  /**
   * Train language guesser.
   *
   * @param set Training set.
   * @return this
   */
  Model train(TrainingSet set);
}
