package com.chachazhan.analyzer.smart.core;

import lombok.val;

import java.util.Arrays;

/**
 * @author jack
 * @date 27/12/2017
 * @time 20:58
 */
class LetterSegmenter implements Segmenter {

  static final String SEGMENTER_NAME = "LETTER_SEGMENTER";

  private static final char[] LETTER_CONNECTOR = new char[]{'#', '&', '+', '.', '@', '_'};
  private static final char[] NUMBER_CONNECTOR = new char[]{',', '.'};

  private int begin;
  private int end;
  private int englishBegin;
  private int englishEnd;
  private int arabicBegin;
  private int arabicEnd;

  LetterSegmenter() {
    Arrays.sort(LETTER_CONNECTOR);
    Arrays.sort(NUMBER_CONNECTOR);
    this.begin = -1;
    this.end = -1;
    this.englishBegin = -1;
    this.englishEnd = -1;
    this.arabicBegin = -1;
    this.arabicEnd = -1;
  }

  @Override
  public void analyze(AnalyzeContext context) {
    boolean bufferLockFlag = false;
    bufferLockFlag = this.processEnglishLetter(context) || bufferLockFlag;
    bufferLockFlag = this.processArabicLetter(context) || bufferLockFlag;
//    bufferLockFlag = this.processMixLetter(context) || bufferLockFlag;

    if (bufferLockFlag) {
      context.lockBuffer(SEGMENTER_NAME);
    } else {
      context.unlockBuffer(SEGMENTER_NAME);
    }

  }

  @Override
  public void reset() {
    this.begin = -1;
    this.end = -1;
    this.englishBegin = -1;
    this.englishEnd = -1;
    this.arabicBegin = -1;
    this.arabicEnd = -1;
  }

  private boolean processMixLetter(AnalyzeContext context) {
    if (this.begin == -1) {
      if (CharacterUtils.CHAR_ARABIC == context.getCurrentCharType() || CharacterUtils.CHAR_ENGLISH == context.getCurrentCharType()) {
        this.begin = context.getCursor();
        this.end = this.begin;
      }
    } else {
      if (CharacterUtils.CHAR_ARABIC == context.getCurrentCharType() || CharacterUtils.CHAR_ENGLISH == context.getCurrentCharType()) {
        this.end = context.getCursor();
      } else if (CharacterUtils.CHAR_USELESS == context.getCurrentCharType() && this.isLetterConnector(context.getCurrentChar())) {
        this.end = context.getCursor();
      } else {
        val lexeme = new Lexeme(context.getBufferOffset(), this.begin, this.end - this.begin + 1, LexemeType.TYPE_LETTER);
        context.addLexeme(lexeme);
        this.begin = -1;
        this.end = -1;
      }
    }

    if (context.isBufferConsumed() && (this.begin != -1 && this.end != -1)) {
      val lexeme = new Lexeme(context.getBufferOffset(), this.begin, this.end - this.begin + 1, LexemeType.TYPE_LETTER);
      context.addLexeme(lexeme);
      this.begin = -1;
      this.end = -1;
    }

    return this.begin != -1 || this.end != -1;
  }

  private boolean processEnglishLetter(AnalyzeContext context) {
    if (this.englishBegin == -1) {
      if (CharacterUtils.CHAR_ENGLISH == context.getCurrentCharType()) {
        this.englishBegin = context.getCursor();
        this.englishEnd = this.englishBegin;
      }
    } else {
      if (CharacterUtils.CHAR_ENGLISH == context.getCurrentCharType()) {
        this.englishEnd = context.getCursor();
      } else {
        val lexeme = new Lexeme(context.getBufferOffset(), this.englishBegin, this.englishEnd - this.englishBegin + 1, LexemeType.TYPE_ENGLISH);
        context.addLexeme(lexeme);
        this.englishBegin = -1;
        this.englishEnd = -1;
      }
    }

    if (context.isBufferConsumed() && (this.englishBegin != -1 && this.englishEnd != -1)) {
      val lexeme = new Lexeme(context.getBufferOffset(), this.englishBegin, this.englishEnd - this.englishBegin + 1, LexemeType.TYPE_ENGLISH);
      context.addLexeme(lexeme);
      this.englishBegin = -1;
      this.englishEnd = -1;
    }

    return this.englishBegin != -1 || this.englishEnd != -1;
  }

  private boolean processArabicLetter(AnalyzeContext context) {
    if (this.arabicBegin == -1) {
      if (CharacterUtils.CHAR_ARABIC == context.getCurrentCharType()) {
        this.arabicBegin = context.getCursor();
        this.arabicEnd = this.arabicBegin;
      }
    } else {
      if (CharacterUtils.CHAR_ARABIC == context.getCurrentCharType()) {
        this.arabicEnd = context.getCursor();
      } else if (CharacterUtils.CHAR_USELESS == context.getCurrentCharType() && this.isNumberConnector(context.getCurrentChar())) {
        // do nothing
      } else {
        val lexeme = new Lexeme(context.getBufferOffset(), this.arabicBegin, this.arabicEnd - this.arabicBegin + 1, LexemeType.TYPE_ARABIC);
        context.addLexeme(lexeme);
        this.arabicBegin = -1;
        this.arabicEnd = -1;
      }
    }

    if (context.isBufferConsumed() && (this.arabicBegin != -1 && this.arabicEnd != -1)) {
      val lexeme = new Lexeme(context.getBufferOffset(), this.arabicBegin, this.arabicEnd - this.arabicBegin + 1, LexemeType.TYPE_ARABIC);
      context.addLexeme(lexeme);
      this.arabicBegin = -1;
      this.arabicEnd = -1;
    }

    return this.arabicBegin != -1 || this.arabicEnd != -1;
  }

  private boolean isLetterConnector(char input) {
    return Arrays.binarySearch(LETTER_CONNECTOR, input) >= 0;
  }

  private boolean isNumberConnector(char input) {
    return Arrays.binarySearch(NUMBER_CONNECTOR, input) >= 0;
  }

}
