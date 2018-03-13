package com.chachazhan.analyzer.smart.lucene;

import lombok.val;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardFilter;

/**
 * @author jack
 * @date 28/12/2017
 * @time 18:38
 */
public class DelimiterAnalyzer extends Analyzer {

  private char delimiter;

  public DelimiterAnalyzer(char delimiter) {
    this.delimiter = delimiter;
  }

  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    val tokenizer = new DelimiterTokenizer(delimiter);
    val standardFilter = new StandardFilter(tokenizer);
    val lowerCaseFilter = new LowerCaseFilter(standardFilter);
    return new TokenStreamComponents(tokenizer, lowerCaseFilter);
  }

}
