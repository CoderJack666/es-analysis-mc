package org.elasticsearch.index.analysis;

import com.chachazhan.analyzer.smart.lucene.DelimiterTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * @author jack
 * @date 28/12/2017
 * @time 18:49
 */
public class DelimiterTokenizerFactory extends AbstractTokenizerFactory {

  public DelimiterTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    super(indexSettings, name, settings);
  }

  public static DelimiterTokenizerFactory getDelimiterTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    return new DelimiterTokenizerFactory(indexSettings, env, name, settings);
  }

  @Override
  public Tokenizer create() {
    return new DelimiterTokenizer('|');
  }
}
