package com.chachazhan.analyzer.smart.lucene;

import lombok.val;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;

/**
 * @author jack
 * @date 28/12/2017
 * @time 18:32
 */
public class CharacterAnalyzer extends Analyzer {

  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    val characterTokenizer = new CharacterTokenizer();
    val tokenStream = new LowerCaseFilter(characterTokenizer);
    return new TokenStreamComponents(characterTokenizer, tokenStream);
  }

}
