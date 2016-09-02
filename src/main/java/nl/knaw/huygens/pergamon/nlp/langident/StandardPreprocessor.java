package nl.knaw.huygens.pergamon.nlp.langident;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StandardPreprocessor {
  private static final Pattern UNWANTED = Pattern.compile(
    "([,.:;!?&+*/=\\(\\)\\[\\]‘’\"“”½√♃∙∥\\d])|( -)|(- )"
  );

  private static final Pattern WHITESPACE = Pattern.compile("\\s+",
    Pattern.UNICODE_CHARACTER_CLASS);

  /**
   * Normalizes text for language identification.
   * Leaves in apostrophes and dashes in words.
   */
  static String preprocess(CharSequence text) {
    String str = text.toString();

    // Delete roman numerals; \u0186 is 'Ɔ'.
    str = str.replaceAll("\\b[IVXLDCMƆ]{2,}\\b", "");

    str = str.toLowerCase();

    // Delete initials in personal names.
    str = str.replaceAll("\\b[a-z][.:]", "");

    // Delete punctuation, quotes, math and digits.
    Matcher matcher = UNWANTED.matcher(str);
    str = matcher.replaceAll(" ");

    // Normalize whitespace.
    matcher = WHITESPACE.matcher(str);
    str = matcher.replaceAll(" ");

    return str.isEmpty() ? "" : " " + str + " ";
  }
}
