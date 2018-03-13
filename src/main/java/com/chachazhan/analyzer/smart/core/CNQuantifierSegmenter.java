package com.chachazhan.analyzer.smart.core;

import com.chachazhan.analyzer.smart.dic.Dictionary;
import com.chachazhan.analyzer.smart.dic.Hit;
import lombok.val;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 中文数量词子分析器
 *
 * @author jack
 * @date 27/12/2017
 * @time 20:36
 */
class CNQuantifierSegmenter implements Segmenter {

  static final String SEGMENTER_NAME = "QUAN_SEGMENTER";

  private static String cnNum = "一二两三四五六七八九十零壹贰叁肆伍陆柒捌玖拾百千万亿拾佰仟萬億兆卅廿";
  private static Set<Character> cnNumberChars = new HashSet<>();

  static {
    char[] cnNumChars = cnNum.toCharArray();
    for (char cnNumChar : cnNumChars) {
      cnNumberChars.add(cnNumChar);
    }
  }

  private int begin;

  private int end;

  private List<Hit> countHits;

  CNQuantifierSegmenter() {
    this.begin = -1;
    this.end = -1;
    this.countHits = new LinkedList<>();
  }

  @Override
  public void analyze(AnalyzeContext context) {
    this.processCNumber(context);
    this.processCount(context);

    if (this.begin == -1 && this.end == -1 && this.countHits.isEmpty()) {
      context.unlockBuffer(SEGMENTER_NAME);
    } else {
      context.lockBuffer(SEGMENTER_NAME);
    }
  }

  @Override
  public void reset() {
    this.begin = -1;
    this.end = -1;
    this.countHits.clear();
  }

  private void processCNumber(AnalyzeContext context) {
    if (this.begin == -1 && this.end == -1) {
      if (CharacterUtils.CHAR_CHINESE == context.getCurrentCharType() && cnNumberChars.contains(context.getCurrentChar())) {
        this.begin = context.getCursor();
        this.end = context.getCursor();
      }
    } else {
      if (CharacterUtils.CHAR_CHINESE == context.getCurrentCharType() && cnNumberChars.contains(context.getCurrentChar())) {
        this.end = context.getCursor();
      } else {
        this.outputNumberLexeme(context);
        this.begin = -1;
        this.end = -1;
      }
    }

    if (context.isBufferConsumed() && this.begin != -1 && this.end != -1) {
      outputNumberLexeme(context);
      this.begin = -1;
      this.end = -1;
    }
  }

  private void processCount(AnalyzeContext context) {
    if (!this.needCountScan(context)) {
      return;
    }

    if (CharacterUtils.CHAR_CHINESE == context.getCurrentCharType()) {
      if (!this.countHits.isEmpty()) {
        val tmpHitArray = this.countHits.toArray(new Hit[this.countHits.size()]);
        for (Hit hit : tmpHitArray) {
          hit = Dictionary.getSingleton().matchWithHit(context.getSegmentBuffer(), context.getCursor(), hit);
          if (hit.isMatch()) {
            val lexeme = new Lexeme(context.getBufferOffset(), hit.getBegin(), context.getCursor() - hit.getBegin() + 1, LexemeType.TYPE_COUNT);
            context.addLexeme(lexeme);
            if (!hit.isPrefix()) {
              this.countHits.remove(hit);
            }
          } else if (hit.isUnmatch()) {
            this.countHits.remove(hit);
          }
        }
      }

      val singleCharHit = Dictionary.getSingleton().matchInQuantifierDict(context.getSegmentBuffer(), context.getCursor(), 1);
      if (singleCharHit.isMatch()) {
        val lexeme = new Lexeme(context.getBufferOffset(), context.getCursor(), 1, LexemeType.TYPE_COUNT);
        context.addLexeme(lexeme);
        if (singleCharHit.isPrefix()) {
          this.countHits.add(singleCharHit);
        }
      } else if (singleCharHit.isPrefix()) {
        this.countHits.add(singleCharHit);
      }
    } else {
      this.countHits.clear();
    }

    if (context.isBufferConsumed()) {
      this.countHits.clear();
    }
  }

  private boolean needCountScan(AnalyzeContext context) {
    if ((this.begin != -1 && this.end != -1) || !this.countHits.isEmpty()) {
      return true;
    } else {
      if (!context.getAmbiguousResult().isEmpty()) {
        val lexeme = context.getAmbiguousResult().peekLast();
        return (LexemeType.TYPE_CNUM == lexeme.getType() || LexemeType.TYPE_ARABIC == lexeme.getType()) && (lexeme.getBegin() + lexeme.getLength() == context.getCursor());
      }
    }
    return false;
  }

  private void outputNumberLexeme(AnalyzeContext context) {
    if (this.begin > -1 && this.end > -1) {
      val lexeme = new Lexeme(context.getBufferOffset(), this.begin, this.end - this.begin + 1, LexemeType.TYPE_CNUM);
      context.addLexeme(lexeme);
    }
  }

}
