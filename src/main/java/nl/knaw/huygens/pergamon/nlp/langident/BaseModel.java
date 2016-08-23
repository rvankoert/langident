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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

/**
 * Abstract base class for machine-learned language guessers, i.e., string classifiers
 * based on character n-gram features.
 * <p>
 * Handles n-gram extraction and problem-specific preprocessing.
 */
abstract class BaseModel implements Model {
  @Override
  public String predictBest(CharSequence doc) {
    return predictStream(doc).max(comparing(Prediction::getScore)).get().getLabel();
  }

  @Override
  public List<Prediction> predictScores(CharSequence doc) {
    return predictStream(doc).sorted(comparing(pred -> -pred.getScore())).collect(Collectors.toList());
  }

  /**
   * The actual prediction function.
   */
  protected abstract Stream<Prediction> predictStream(CharSequence doc);
}
