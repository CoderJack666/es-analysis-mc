package com.chachazhan.analyzer.smart.core;

import com.chachazhan.analyzer.smart.config.Configuration;
import com.chachazhan.analyzer.smart.dic.Dictionary;
import lombok.Getter;
import lombok.experimental.var;
import lombok.val;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * @author jack
 * @date 27/12/2017
 * @time 14:25
 */
@Getter
public class AnalyzeContext implements LexemeType {

  private static final int BUFFER_SIZE = 4096;

  private static final int BUFFER_EXHAUST_CRITICAL = 100;

  private char[] segmentBuffer;

  private int[] charTypes;

  private int bufferOffset;

  private int cursor;

  private int available;

  private Set<String> bufferLock;

  private QuickSortSet ambiguousResult;

  private Map<Integer, LexemePath> lexemePathMap;

  private LinkedList<Lexeme> result;

  private final Configuration configuration;

  public AnalyzeContext(Configuration configuration) {
    this.configuration = configuration;
    this.segmentBuffer = new char[BUFFER_SIZE];
    this.charTypes = new int[BUFFER_SIZE];
    this.bufferLock = new HashSet<>();
    this.ambiguousResult = new QuickSortSet();
    this.lexemePathMap = new HashMap<>();
    this.result = new LinkedList<>();
  }

  public char getCurrentChar() {
    return this.segmentBuffer[this.cursor];
  }

  public int getCurrentCharType() {
    return this.charTypes[this.cursor];
  }

  public int fillBuffer(Reader reader) throws IOException {
    int readCount = 0;
    if (this.bufferOffset == 0) {
      readCount = reader.read(segmentBuffer);
    } else {
      int offset = this.available - this.cursor;
      if (offset > 0) {
        System.arraycopy(this.segmentBuffer, this.cursor, this.segmentBuffer, 0, offset);
        readCount = offset;
      }
      readCount += reader.read(this.segmentBuffer, offset, BUFFER_SIZE - offset);
    }
    this.available = readCount;
    this.cursor = 0;
    return readCount;
  }

  public void lockBuffer(String segmenterName) {
    this.bufferLock.add(segmenterName);
  }

  public void unlockBuffer(String segmenterName) {
    this.bufferLock.remove(segmenterName);
  }

  public boolean isBufferLocked() {
    return this.bufferLock.size() > 0;
  }

  public boolean isBufferConsumed() {
    return this.cursor == this.available - 1;
  }

  public boolean needRefillBuffer() {
    return this.available == BUFFER_SIZE && this.cursor < this.available - 1 &&
      this.cursor > this.available - BUFFER_EXHAUST_CRITICAL && !this.isBufferLocked();
  }

  public void markBufferOffset() {
    this.bufferOffset += this.cursor;
  }

  public void initCursor() {
    this.cursor = 0;
    char currentChar = segmentBuffer[cursor];
    this.segmentBuffer[cursor] = CharacterUtils.regularize(currentChar);
    this.charTypes[cursor] = CharacterUtils.getCharType(currentChar);
  }

  public boolean moveCursor() {
    if (this.cursor < this.available - 1) {
      this.cursor++;
      char c = segmentBuffer[cursor];
      this.segmentBuffer[cursor] = CharacterUtils.regularize(c);
      this.charTypes[cursor] = CharacterUtils.getCharType(c);
      return true;
    }
    return false;
  }

  public void addLexeme(Lexeme lexeme) {
    this.ambiguousResult.addLexeme(lexeme);
  }

  public void addLexemePath(LexemePath path) {
    if (path != null) {
      this.lexemePathMap.put(path.getBegin(), path);
    }
  }

  public void outputToResult() {
    var index = 0;
    while (index <= this.cursor) {
      if (CharacterUtils.CHAR_USELESS == this.charTypes[index]) {
        index++;
        continue;
      }
      val path = this.lexemePathMap.get(index);
      if (path != null) {
        var lexeme = path.pollFirst();
        while (lexeme != null) {
          this.result.add(lexeme);
          index = lexeme.getBegin() + lexeme.getLength();
          lexeme = path.pollFirst();
          if (lexeme != null) {
            for (; index < lexeme.getBegin(); index++) {
              this.outputSingleCJK(index);
            }
          }
        }
      } else {
        this.outputSingleCJK(index);
        index++;
      }
    }
    this.lexemePathMap.clear();
  }

  private void outputSingleCJK(int index) {
    if (CharacterUtils.CHAR_CHINESE == this.charTypes[index]) {
      val singleCharLexeme = new Lexeme(this.bufferOffset, index, 1, TYPE_CNCHAR);
      this.result.add(singleCharLexeme);
    } else if (CharacterUtils.CHAR_OTHER_CJK == this.charTypes[index]) {
      val singleCharLexeme = new Lexeme(this.bufferOffset, index, 1, LexemeType.TYPE_OTHER_CJK);
      this.result.add(singleCharLexeme);
    }
  }

  public Lexeme getNextLexeme() {
    Lexeme result = this.result.pollFirst();
    while (result != null) {
      this.compound(result);
      if (Dictionary.getSingleton().isStopWord(this.segmentBuffer, result.getBegin(), result.getLength())) {
        result = this.result.pollFirst();
      } else {
        result.setText(String.valueOf(segmentBuffer, result.getBegin(), result.getLength()));
        break;
      }
    }
    return result;
  }

  public void reset() {
    this.bufferLock.clear();
    this.ambiguousResult = new QuickSortSet();
    this.available = 0;
    this.bufferOffset = 0;
    this.charTypes = new int[BUFFER_SIZE];
    this.cursor = 0;
    this.result.clear();
    this.segmentBuffer = new char[BUFFER_SIZE];
    this.lexemePathMap.clear();
  }

  private void compound(Lexeme result) {
    if (!this.configuration.isUseSmart()) {
      return;
    }
    if (!this.result.isEmpty()) {
      if (TYPE_ARABIC == result.getType()) {
        val nextLexeme = this.result.peekFirst();
        var appendOk = false;
        if (TYPE_CNUM == nextLexeme.getType()) {
          appendOk = result.append(nextLexeme, LexemeType.TYPE_CNUM);
        } else if (TYPE_COUNT == nextLexeme.getType()) {
          appendOk = result.append(nextLexeme, LexemeType.TYPE_CQUAN);
        }
        if (appendOk) {
          this.result.pollFirst();
        }
      }

      if (TYPE_CNUM == result.getType() && !this.result.isEmpty()) {
        val nextLexeme = this.result.peekFirst();
        var appendOk = false;
        if (TYPE_COUNT == nextLexeme.getType()) {
          appendOk = result.append(nextLexeme, LexemeType.TYPE_CQUAN);
        }
        if (appendOk) {
          this.result.pollFirst();
        }
      }
    }
  }

}
