package nl.knaw.huygens.pergamon.nlp.langident;

/*
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

import nl.knaw.huygens.algomas.nlp.NGrams;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Abstract base class for machine-learned language guessers, i.e., string classifiers
 * based on character n-gram features.
 * <p>
 * Handles n-gram extraction and problem-specific preprocessing.
 */
abstract class BaseTrainer implements Trainer {
  private Function<CharSequence, CharSequence> preprocessor;

  BaseTrainer() {
    this(StandardPreprocessor::preprocess);
  }

  BaseTrainer(Function<CharSequence, CharSequence> preprocessor) {
    this.preprocessor = preprocessor;
  }

  // Sizes of n-grams.
  protected int minN = 2, maxN = 7;

  /**
   * Extract n-gram features from doc after preprocessing.
   *
   * @param doc Any piece of text.
   * @return A sequential stream of n-grams, with minN <= n <= maxN.
   */
  protected final Stream<CharSequence> features(CharSequence doc) {
    return NGrams.ofChars(minN, maxN, preprocessor.apply(doc.toString()));
  }

  /**
   * Train language guesser (actual implementation).
   *
   * @param docs   List of documents.
   * @param labels List of labels corresponding to documents.
   * @return this
   */
  protected abstract Model train(List<CharSequence> docs, List<String> labels);

  @Override
  public final Model train(TrainingSet set) {
    return train(set.getDocs(), set.getLabels());
  }
}
