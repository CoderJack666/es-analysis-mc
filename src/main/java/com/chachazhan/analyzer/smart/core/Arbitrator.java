package com.chachazhan.analyzer.smart.core;

import lombok.SneakyThrows;
import lombok.experimental.var;
import lombok.val;

import java.util.Stack;
import java.util.TreeSet;

/**
 * @author jack
 * @date 27/12/2017
 * @time 21:22
 */
class Arbitrator {

  void process(AnalyzeContext context) {
    val useSmart = context.getConfiguration().isUseSmart();
    val ambiguousLexemes = context.getAmbiguousResult();
    var ambiguousLexeme = ambiguousLexemes.pollFirst();
    var crossPath = new LexemePath();
    while (ambiguousLexeme != null) {
      if (!crossPath.addCrossLexeme(ambiguousLexeme)) {
        addLexemePath(context, useSmart, crossPath);
        crossPath = new LexemePath();
        crossPath.addCrossLexeme(ambiguousLexeme);
      }
      ambiguousLexeme = ambiguousLexemes.pollFirst();
    }

    addLexemePath(context, useSmart, crossPath);
  }

  private void addLexemePath(AnalyzeContext context, boolean useSmart, LexemePath crossPath) {
    if (crossPath.getSize() == 1 || !useSmart) {
      context.addLexemePath(crossPath);
    } else {
      val headCell = crossPath.getHead();
      val judgeResult = this.judge(headCell, crossPath.getPathLength());
      context.addLexemePath(judgeResult);
    }
  }

  @SneakyThrows
  private LexemePath judge(QuickSortSet.Cell cell, int fullTextLength) {
    val pathOptions = new TreeSet<LexemePath>();
    val option = new LexemePath();
    val lexemeStack = this.forwardPath(cell, option);

    pathOptions.add(option.clone());
    QuickSortSet.Cell c;
    while (!lexemeStack.isEmpty()) {
      c = lexemeStack.pop();
      this.backwardPath(c.getLexeme(), option);
      this.forwardPath(c, option);
      pathOptions.add(option.clone());
    }

    return pathOptions.first();
  }

  private Stack<QuickSortSet.Cell> forwardPath(QuickSortSet.Cell lexemeCell, LexemePath option) {
    val conflictStack = new Stack<QuickSortSet.Cell>();
    var cell = lexemeCell;
    while (cell != null && cell.getLexeme() != null) {
      if (!option.addNotCrossLexeme(cell.getLexeme())) {
        conflictStack.push(cell);
      }
      cell = cell.getNext();
    }
    return conflictStack;
  }

  private void backwardPath(Lexeme lexeme, LexemePath option) {
    while (option.checkCross(lexeme)) {
      option.removeTail();
    }
  }

}
