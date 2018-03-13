package com.chachazhan.analyzer.smart.core;

/**
 * @author jack
 * @date 27/12/2017
 * @time 18:14
 */
public final class CharacterUtils {

  public static final int CHAR_USELESS = 0;
  public static final int CHAR_ARABIC = 0x00000001;
  public static final int CHAR_ENGLISH = 0x00000002;
  public static final int CHAR_CHINESE = 0x00000004;
  public static final int CHAR_OTHER_CJK = 0x00000008;

  private CharacterUtils() {}

  public static int getCharType(char input) {
    if (input >= '0' && input <= '9') {
      return CHAR_ARABIC;
    } else if ((input >= 'a' && input <= 'z') || (input >= 'A' && input <= 'Z')) {
      return CHAR_ENGLISH;
    } else {
      Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(input);
      if (unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
          unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
          unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
        return CHAR_CHINESE;
      } else if (unicodeBlock == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
                 unicodeBlock == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                 unicodeBlock == Character.UnicodeBlock.HANGUL_JAMO ||
                 unicodeBlock == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO ||
                 unicodeBlock == Character.UnicodeBlock.HIRAGANA ||
                 unicodeBlock == Character.UnicodeBlock.KATAKANA ||
                 unicodeBlock == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS) {
        return CHAR_OTHER_CJK;
      }
    }
    return CHAR_USELESS;
  }

  public static char regularize(char input) {
    if (input == 12288) {
      input = (char) 32;
    } else if (input > 65280 && input < 65357) {
      input = (char) (input - 65248);
    } else if (input >= 'A' && input <= 'Z') {
      input += 32;
    }
    return input;
  }

}
