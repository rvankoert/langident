package nl.knaw.huygens.pergamon.nlp.langident;

public interface Trainer {
  /**
   * Train language guesser.
   *
   * @param set Training set.
   * @return this
   */
  Model train(TrainingSet set);
}
