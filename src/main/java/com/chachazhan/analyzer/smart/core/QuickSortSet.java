package com.chachazhan.analyzer.smart.core;

import com.chachazhan.analyzer.smart.util.AssertUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.var;
import lombok.val;

/**
 * @author jack
 * @date 27/12/2017
 * @time 12:15
 */
public class QuickSortSet {

  @Getter
  private Cell head;
  @Getter
  private Cell tail;
  @Getter
  private int size;

  public QuickSortSet() {
    this.size = 0;
  }

  boolean addLexeme(Lexeme lexeme) {
    val newCell = new Cell(lexeme);
    if (this.size == 0) {
      this.head = newCell;
      this.tail = newCell;
      this.size++;
      return true;
    } else {
      if (this.tail.compareTo(newCell) == 0) {
        return false;
      } else if (this.tail.compareTo(newCell) < 0) {
        this.tail.setNext(newCell);
        newCell.setPrev(this.tail);
        this.tail = newCell;
        this.size++;
        return true;
      } else if (this.head.compareTo(newCell) > 0) {
        this.head.setPrev(newCell);
        newCell.setNext(this.head);
        this.head = newCell;
        this.size++;
        return true;
      } else {
        var index = this.tail;
        while (index != null && index.compareTo(newCell) > 0) {
          index = index.getPrev();
        }

        AssertUtils.notNull(index, "插入词元到集合中发生异常: 出现非正常null");

        if (index.compareTo(newCell) == 0) {
          return false;
        } else if (index.compareTo(newCell) < 0) {
          newCell.setPrev(index);
          newCell.setNext(index.getNext());
          index.getNext().setPrev(newCell);
          index.setNext(newCell);
          this.size++;
          return true;
        }
      }
    }
    return false;
  }

  public Lexeme peekFirst() {
    return this.head == null ? null : this.head.getLexeme();
  }

  public Lexeme pollFirst() {
    if (this.size == 1) {
      val first = this.head.getLexeme();
      this.head = null;
      this.tail = null;
      this.size--;
      return first;
    }

    if (this.size > 1) {
      val first = this.head.getLexeme();
      this.head = this.head.getNext();
      this.size--;
      return first;
    }

    return null;
  }

  public Lexeme peekLast() {
    return this.tail == null ? null : this.tail.getLexeme();
  }

  public Lexeme pollLast() {
    if (this.size == 1) {
      val last = this.tail.getLexeme();
      this.head = null;
      this.tail = null;
      this.size--;
      return last;
    }

    if (this.size > 1) {
      val last = this.tail.getLexeme();
      this.tail = this.tail.getPrev();
      this.size--;
      return last;
    }

    return null;
  }

  public boolean isEmpty() {
    return this.size == 0;
  }

  @Data
  public static class Cell implements Comparable<Cell> {
    private Cell prev;
    private Cell next;
    @NonNull
    private Lexeme lexeme;

    @Override
    public int compareTo(Cell cell) {
      return this.lexeme.compareTo(cell.getLexeme());
    }
  }

}
