package com.chachazhan.analyzer.smart.lucene;

import org.apache.lucene.analysis.util.CharTokenizer;

/**
 * @author jack
 * @date 28/12/2017
 * @time 18:35
 */
public class DelimiterTokenizer extends CharTokenizer {

  private char delimiter;

  public DelimiterTokenizer(char delimiter) {
    super();
    this.delimiter = delimiter;
  }

  @Override
  protected boolean isTokenChar(int c) {
    return c != delimiter;
  }

}
