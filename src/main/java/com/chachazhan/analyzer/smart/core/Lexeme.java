package com.chachazhan.analyzer.smart.core;

import lombok.Data;
import lombok.NonNull;

import java.util.Objects;

/**
 * @author jack
 * @date 27/12/2017
 * @time 12:20
 */
@Data
public class Lexeme implements LexemeType, Comparable<Lexeme> {

  @NonNull
  private int offset;

  @NonNull
  private int begin;

  @NonNull
  private int length;

  private String text;

  @NonNull
  private int type;

  public String getText() {
    return this.text == null ? "" : this.text;
  }

  public void setText(String text) {
    if (text == null) {
      this.text = "";
      this.length = 0;
    } else {
      this.text = text;
      this.length = text.length();
    }
  }

  public int getBeginPosition() {
    return this.offset + this.begin;
  }

  public int getEndPosition() {
    return this.offset + this.begin + this.length;
  }

  public boolean append(Lexeme lexeme, int type) {
    if (lexeme != null && this.getEndPosition() == lexeme.getBeginPosition()) {
      this.length += lexeme.getLength();
      this.type = type;
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return
      this.getBeginPosition() +
      "-" +
      this.getEndPosition() +
      " : " +
      this.text +
      " : \t" +
      this.getLexemeTypeString(this.type);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    if (!super.equals(other)) {
      return false;
    }
    Lexeme lexeme = (Lexeme) other;
    return offset == lexeme.offset &&
      begin == lexeme.begin &&
      length == lexeme.length;
  }

  @Override
  public int hashCode() {
    return (getBeginPosition() * 37) + (getEndPosition() * 31) +
      ((getBeginPosition() * getEndPosition()) % getLength()) * 11;
  }

  @Override
  public int compareTo(Lexeme other) {
    if (this.begin < other.getBegin()) {
      return -1;
    } else if (this.begin == other.getBegin()) {
      return Integer.compare(other.getLength(), this.length);
    } else {
      return 1;
    }
  }
}
