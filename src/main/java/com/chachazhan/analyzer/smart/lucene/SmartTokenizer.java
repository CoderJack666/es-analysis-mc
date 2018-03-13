package com.chachazhan.analyzer.smart.lucene;

import com.chachazhan.analyzer.smart.config.Configuration;
import com.chachazhan.analyzer.smart.core.SmartSegmenter;
import lombok.experimental.var;
import lombok.val;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

/**
 * @author jack
 * @date 27/12/2017
 * @time 21:50
 */
public final class SmartTokenizer extends Tokenizer {

  private final CharTermAttribute charTermAttribute;
  private final OffsetAttribute offsetAttribute;
  private final TypeAttribute typeAttribute;
  private final PositionIncrementAttribute positionIncrementAttribute;

  private int endPosition;
  private int skippedPositions;

  private SmartSegmenter smartSegmenter;

  public SmartTokenizer(Configuration configuration) {
    super();
    this.charTermAttribute = addAttribute(CharTermAttribute.class);
    this.offsetAttribute = addAttribute(OffsetAttribute.class);
    this.typeAttribute = addAttribute(TypeAttribute.class);
    this.positionIncrementAttribute = addAttribute(PositionIncrementAttribute.class);
    this.smartSegmenter = new SmartSegmenter(input, configuration);
  }

  @Override
  public boolean incrementToken() throws IOException {
    clearAttributes();
    this.skippedPositions = 0;
    var nextLexeme = this.smartSegmenter.next();
    if (nextLexeme != null) {
      positionIncrementAttribute.setPositionIncrement(skippedPositions + 1);
      charTermAttribute.append(nextLexeme.getText());
      charTermAttribute.setLength(nextLexeme.getLength());
      offsetAttribute.setOffset(correctOffset(nextLexeme.getBeginPosition()), correctOffset(nextLexeme.getEndPosition()));
      endPosition = nextLexeme.getEndPosition();
      typeAttribute.setType(nextLexeme.getLexemeTypeString(nextLexeme.getType()));
      return true;
    }
    return false;
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    this.smartSegmenter.reset(input);
    skippedPositions = 0;
  }

  @Override
  public final void end() throws IOException {
    super.end();
    val finalOffset = correctOffset(this.endPosition);
    this.offsetAttribute.setOffset(finalOffset, finalOffset);
    this.positionIncrementAttribute.setPositionIncrement(positionIncrementAttribute.getPositionIncrement() + skippedPositions);
  }
}
