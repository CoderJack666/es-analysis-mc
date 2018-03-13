package com.chachazhan.analyzer.smart.core;

import com.chachazhan.analyzer.smart.dic.Dictionary;
import com.chachazhan.analyzer.smart.dic.Hit;
import lombok.val;

import java.util.LinkedList;
import java.util.List;

/**
 * @author jack
 * @date 27/12/2017
 * @time 20:26
 */
class CJKSegmenter implements Segmenter {

  static final String SEGMENTER_NAME = "CJK_SEGMENTER";

  private List<Hit> tmpHits;

  CJKSegmenter() {
    this.tmpHits = new LinkedList<>();
  }

  @Override
  public void analyze(AnalyzeContext context) {
    if (CharacterUtils.CHAR_USELESS != context.getCurrentCharType()) {
      if (!this.tmpHits.isEmpty()) {
        val tmpHitArray = this.tmpHits.toArray(new Hit[this.tmpHits.size()]);
        for (Hit hit : tmpHitArray) {
          hit = Dictionary.getSingleton().matchWithHit(context.getSegmentBuffer(), context.getCursor(), hit);
          if (hit.isMatch()) {
            val lexeme = new Lexeme(context.getBufferOffset(), hit.getBegin(), context.getCursor() - hit.getBegin() + 1, LexemeType.TYPE_CNWORD);
            context.addLexeme(lexeme);
            if (!hit.isPrefix()) {
              this.tmpHits.remove(hit);
            }
          } else if (hit.isUnmatch()) {
            this.tmpHits.remove(hit);
          }
        }
      }

      val singleCharHit = Dictionary.getSingleton().matchInMainDict(context.getSegmentBuffer(), context.getCursor(), 1);
      if (singleCharHit.isMatch()) {
        val lexeme = new Lexeme(context.getBufferOffset(), context.getCursor(), 1, LexemeType.TYPE_CNWORD);
        context.addLexeme(lexeme);
        if (singleCharHit.isPrefix()) {
          this.tmpHits.add(singleCharHit);
        }
      } else if (singleCharHit.isPrefix()) {
        this.tmpHits.add(singleCharHit);
      }
    } else {
      this.tmpHits.clear();
    }

    if (context.isBufferConsumed()) {
      this.tmpHits.clear();
    }

    if (this.tmpHits.size() == 0) {
      context.unlockBuffer(SEGMENTER_NAME);
    } else {
      context.lockBuffer(SEGMENTER_NAME);
    }
  }

  @Override
  public void reset() {
    this.tmpHits.clear();
  }
}
