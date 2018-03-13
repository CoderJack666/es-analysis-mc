package com.chachazhan.analyzer.smart.lucene;

import com.chachazhan.analyzer.smart.core.Lexeme;
import com.chachazhan.analyzer.smart.core.LexemeType;
import lombok.val;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jack
 * @date 28/12/2017
 * @time 18:24
 */
public class CharacterTokenizer extends Tokenizer {

  private final CharTermAttribute charTermAttribute;
  private final OffsetAttribute offsetAttribute;
  private final TypeAttribute typeAttribute;

  private char[] context = new char[64];
  private int cursor = 0;
  private int length = 0;
  private int finalEnd = 0;
  private List<Lexeme> lexemes = new ArrayList<>();

  public CharacterTokenizer() {
    super();
    this.charTermAttribute = addAttribute(CharTermAttribute.class);
    this.offsetAttribute = addAttribute(OffsetAttribute.class);
    this.typeAttribute = addAttribute(TypeAttribute.class);
  }

  @Override
  public final boolean incrementToken() throws IOException {
    clearAttributes();
    if (cursor < length) {
      val lexeme = lexemes.get(cursor);
      charTermAttribute.append(lexeme.getText());
      charTermAttribute.setLength(lexeme.getLength());
      typeAttribute.setType(lexeme.getLexemeTypeString(lexeme.getType()));
      offsetAttribute.setOffset(lexeme.getBeginPosition(), lexeme.getEndPosition());
      cursor++;
      return true;
    }
    return false;
  }

  private int setResult() {
    Lexeme lexeme;
    for (int i = 0; i < this.finalEnd; i++) {
      char c = context[i];
      if (Character.isLetterOrDigit(c)) {
        lexeme = new Lexeme(i, 0, 1, LexemeType.TYPE_LETTER_OR_DIGIT);
        lexeme.setText(String.valueOf(c));
        lexemes.add(lexeme);
      }
    }
    return lexemes.size();
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    this.cursor = 0;
    this.finalEnd = this.input.read(context);
    this.lexemes.clear();
    this.length = setResult();
  }

  @Override
  public void end() throws IOException {
    super.end();
    val finalOffset = this.correctOffset(this.finalEnd);
    this.offsetAttribute.setOffset(finalOffset, finalOffset);
  }

}
