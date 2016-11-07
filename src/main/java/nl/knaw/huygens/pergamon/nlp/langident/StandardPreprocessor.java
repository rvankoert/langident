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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StandardPreprocessor {
  private static final Pattern UNWANTED = Pattern.compile("([,.:;!?&+*/=‘’\"“”«»½√♃∙∥\\d])|( -)|(- )");
  private static final Pattern WHITESPACE = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);

  /**
   * Normalizes text for language identification.
   * Leaves in apostrophes and dashes in words.
   */
  static String preprocess(CharSequence text) {
    String str = text.toString();

    // Delete Roman numerals.
    str = str.replaceAll("\\b[IVXLDCMƆ]{2,}\\b", "");

    str = str.toLowerCase();

    // Delete initials in personal names.
    str = str.replaceAll("\\b[a-z][.:]", "");

    // Delete brackets and parenthesis - they may be intra-word.
    str = str.replaceAll("[\\[\\]\\(\\)]", "");

    // Delete punctuation, quotes, math and digits.
    Matcher matcher = UNWANTED.matcher(str);
    str = matcher.replaceAll(" ");

    // Normalize whitespace.
    matcher = WHITESPACE.matcher(str);
    str = matcher.replaceAll(" ").trim();

    return str.isEmpty() ? "" : " " + str + " ";
  }
}
