package com.chachazhan.analyzer.smart.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jack
 * @date 27/12/2017
 * @time 13:46
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class LexemePath extends QuickSortSet implements Comparable<LexemePath>, Cloneable {

  private int begin;

  private int end;

  private int payloadLength;

  public LexemePath() {
    this.begin = -1;
    this.end = -1;
    this.payloadLength = 0;
  }

  public int getPathLength() {
    return this.end - this.begin;
  }

  public int getXWeight() {
    int result = 1;
    Cell cell = this.getHead();
    while (cell != null && cell.getLexeme() != null) {
      result *= cell.getLexeme().getLength();
      cell = cell.getNext();
    }
    return result;
  }

  public int getPositionWeight() {
    int result = 0;
    int position = 0;
    Cell cell = this.getHead();
    while (cell != null && cell.getLexeme() != null) {
      position++;
      result += position * cell.getLexeme().getLength();
      cell = cell.getNext();
    }
    return result;
  }

  public boolean addCrossLexeme(Lexeme lexeme) {
    if (isEmpty()) {
      addIfEmpty(lexeme);
      return true;
    }
    if (checkCross(lexeme)) {
      this.addLexeme(lexeme);
      if (lexeme.getBegin() + lexeme.getLength() > this.end) {
        this.end = lexeme.getBegin() + lexeme.getLength();
      }
      this.payloadLength = this.end - this.begin;
      return true;
    }
    return false;
  }

  public boolean addNotCrossLexeme(Lexeme lexeme) {
    if (isEmpty()) {
      addIfEmpty(lexeme);
      return true;
    }
    if (!checkCross(lexeme)) {
      this.addLexeme(lexeme);
      this.payloadLength += lexeme.getLength();
      Lexeme head = this.peekFirst();
      this.begin = head.getBegin();
      Lexeme tail = this.peekLast();
      this.end = tail.getBegin() + tail.getLength();
      return true;
    }
    return false;
  }

  public Lexeme removeTail() {
    Lexeme tail = this.pollLast();
    if (isEmpty()) {
      this.end = -1;
      this.begin = -1;
      this.payloadLength = 0;
    } else {
      this.payloadLength -= tail.getLength();
      Lexeme newTail = this.peekLast();
      this.end = newTail.getBegin() + newTail.getLength();
    }
    return tail;
  }

  public boolean checkCross(Lexeme lexeme) {
    return (this.begin <= lexeme.getBegin() && lexeme.getBegin() < this.end) ||
      (lexeme.getBegin() <= this.begin && this.begin < lexeme.getBegin() + lexeme.getLength());
  }

  private void addIfEmpty(Lexeme lexeme) {
    this.addLexeme(lexeme);
    this.begin = lexeme.getBegin();
    this.end = lexeme.getBegin() + lexeme.getLength();
    this.payloadLength += lexeme.getLength();
  }

  @Override
  public LexemePath clone() throws CloneNotSupportedException {
    super.clone();
    LexemePath cloned = new LexemePath();
    cloned.setBegin(this.begin);
    cloned.setEnd(this.end);
    cloned.setPayloadLength(this.payloadLength);
    Cell cell = this.getHead();
    while (cell != null && cell.getLexeme() != null) {
      cloned.addLexeme(cell.getLexeme());
      cell = cell.getNext();
    }
    return cloned;
  }

  @Override
  public int compareTo(LexemePath other) {
    if (this.payloadLength > other.getPayloadLength()) {
      return -1;
    } else if (this.payloadLength < other.getPayloadLength()) {
      return 1;
    } else {
      if (this.getSize() < other.getSize()) {
        return -1;
      } else if (this.getSize() > other.getSize()) {
        return 1;
      } else {
        if (this.getPathLength() > other.getPathLength()) {
          return -1;
        } else if (this.getPathLength() < other.getPathLength()) {
          return 1;
        } else {
          if (this.end > other.getEnd()) {
            return -1;
          } else if (this.end < other.getEnd()) {
            return 1;
          } else {
            if (this.getXWeight() > other.getXWeight()) {
              return -1;
            } else if (this.getXWeight() < other.getXWeight()) {
              return 1;
            } else {
              if (this.getPositionWeight() > other.getPositionWeight()) {
                return -1;
              } else if (this.getPositionWeight() < other.getPositionWeight()) {
                return 1;
              }
            }
          }
        }
      }
    }
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("");
    builder.append("Path begin : ").append(this.begin).append("\r\n");
    builder.append("Path end : ").append(this.end).append("\r\n");
    builder.append("Path payload length : ").append(this.payloadLength).append("\r\n");
    Cell head = this.getHead();
    while (head != null) {
      builder.append("Lexeme : ").append(head.getLexeme()).append("\r\n");
      head = head.getNext();
    }
    return builder.toString();
  }
}
