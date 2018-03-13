package com.chachazhan.analyzer.smart.core;

/**
 * @author jack
 * @date 27/12/2017
 * @time 12:23
 */
public interface LexemeType {
  //lexemeType常量
  //未知
  int TYPE_UNKNOWN = 0;
  //英文
  int TYPE_ENGLISH = 1;
  //数字
  int TYPE_ARABIC = 2;
  //英文数字混合
  int TYPE_LETTER = 3;
  //中文词元
  int TYPE_CNWORD = 4;
  //中文单字
  int TYPE_CNCHAR = 64;
  //日韩文字
  int TYPE_OTHER_CJK = 8;
  //中文数词
  int TYPE_CNUM = 16;
  //中文量词
  int TYPE_COUNT = 32;
  //中文数量词
  int TYPE_CQUAN = 48;

  int TYPE_LETTER_OR_DIGIT = 96;

  /**
   * 获取词元类型标示字符串
   * @return String
   */
  default String getLexemeTypeString(int type) {
    switch (type) {
      case TYPE_ENGLISH:
        return "ENGLISH";

      case TYPE_ARABIC:
        return "ARABIC";

      case TYPE_LETTER:
        return "LETTER";

      case TYPE_CNWORD:
        return "CN_WORD";

      case TYPE_CNCHAR:
        return "CN_CHAR";

      case TYPE_OTHER_CJK:
        return "OTHER_CJK";

      case TYPE_COUNT:
        return "COUNT";

      case TYPE_CNUM:
        return "TYPE_CNUM";

      case TYPE_CQUAN:
        return "TYPE_CQUAN";

      case TYPE_LETTER_OR_DIGIT:
        return "LETTER_OR_DIGIT";

      default:
        return "UNKONW";
    }
  }
}
