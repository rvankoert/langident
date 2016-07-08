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

import nl.knaw.huygens.pergamon.nlp.CharNGram;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

/**
 * Abstract base class for machine-learned language guessers, i.e., string classifiers
 * based on character n-gram features.
 * <p>
 * Handles n-gram extraction and problem-specific preprocessing.
 */
public abstract class LanguageGuesser {
  /**
   * Prediction of the language of a document.
   */
  public static class Prediction {
    private final String label;
    private final double score;

    Prediction(String label, double score) {
      this.label = label;
      this.score = score;
    }

    public String getLang() {
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

  // Sizes of n-grams.
  protected int minN = 2, maxN = 7;

  private static final Pattern UNWANTED = Pattern.compile(
    "([,.:;!?&+*/=\\(\\)\\[\\]‘’\"“”½√♃∙∥\\d])|( -)|(- )"
  );

  private static final Pattern WHITESPACE = Pattern.compile("\\s+",
    Pattern.UNICODE_CHARACTER_CLASS);

  /**
   * Normalizes text for language identification.
   * Leaves in apostrophes and dashes in words.
   */
  private static String preprocess(String text) {
    // Delete roman numerals; \u0186 is 'Ɔ'.
    text = text.replaceAll("\\b[IVXLDCM\\u0186]{2,}\\b", "");

    text = text.toLowerCase();

    // Delete initials in personal names.
    text = text.replaceAll("\\b[a-z][.:]", "");

    // Delete punctuation, quotes, math and digits.
    Matcher matcher = UNWANTED.matcher(text);
    text = matcher.replaceAll(" ");

    // Normalize whitespace.
    matcher = WHITESPACE.matcher(text);
    text = matcher.replaceAll(" ");
    return (text.length() == 0) ? "" : " " + text + " ";
  }

  /**
   * Extract n-gram features from doc after preprocessing.
   *
   * @param doc Any piece of text.
   * @return A sequential stream of n-grams, with minN <= n <= maxN.
   */
  protected final Stream<CharSequence> features(CharSequence doc) {
    return CharNGram.generate(preprocess(doc.toString()), minN, maxN);
  }

  /**
   * The set of languages known to this language guesser.
   *
   * @return An immutable Set of language codes.
   */
  public abstract Set<String> languages();

  /**
   * Predict the language of the document doc.
   *
   * @param doc Any piece of text.
   * @return The code for a language assigned to the
   */
  public String predictBest(CharSequence doc) {
    return predictStream(doc).max(comparing(Prediction::getScore)).get().getLang();
  }

  /**
   * Predict the language of doc
   *
   * @param doc A piece of text.
   * @return A list of predictions, sorted from highest-scoring to lowest.
   */
  public List<Prediction> predictScores(CharSequence doc) {
    return predictStream(doc).sorted(comparing(pred -> -pred.getScore())).collect(Collectors.toList());
  }

  /**
   * The actual prediction function.
   *
   * @param doc
   * @return
   */
  abstract protected Stream<Prediction> predictStream(CharSequence doc);

  /**
   * Train language guesser on data.
   *
   * @param docs
   * @param labels
   * @return this
   */
  public abstract LanguageGuesser train(List<CharSequence> docs, List<String> labels);
}
