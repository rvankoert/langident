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
    String s = text.toString();

    // Delete roman numerals; \u0186 is 'Ɔ'.
    s = s.replaceAll("\\b[IVXLDCM\\u0186]{2,}\\b", "");

    s = s.toLowerCase();

    // Delete initials in personal names.
    s = s.replaceAll("\\b[a-z][.:]", "");

    // Delete punctuation, quotes, math and digits.
    Matcher matcher = UNWANTED.matcher(s);
    s = matcher.replaceAll(" ");

    // Normalize whitespace.
    matcher = WHITESPACE.matcher(s);
    s = matcher.replaceAll(" ");
    return (s.length() == 0) ? "" : " " + s + " ";
  }
}
