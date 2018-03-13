package com.chachazhan.analyzer.smart.lucene;

import com.chachazhan.analyzer.smart.config.Configuration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * @author jack
 * @date 27/12/2017
 * @time 10:56
 */
public class SmartAnalyzer extends Analyzer {

  private Configuration configuration;

  public SmartAnalyzer() {}

  public SmartAnalyzer(Configuration configuration) {
    super();
    this.configuration = configuration;
  }

  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer smartTokenizer = new SmartTokenizer(configuration);
    return new TokenStreamComponents(smartTokenizer);
  }
}
