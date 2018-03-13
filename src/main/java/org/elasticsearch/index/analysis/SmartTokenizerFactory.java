package org.elasticsearch.index.analysis;

import com.chachazhan.analyzer.smart.config.Configuration;
import com.chachazhan.analyzer.smart.lucene.SmartTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * @author jack
 * @date 27/12/2017
 * @time 22:04
 */
public class SmartTokenizerFactory extends AbstractTokenizerFactory {

  private Configuration configuration;

  public SmartTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    super(indexSettings, name, settings);
    this.configuration = new Configuration(env, settings);
  }

  public static SmartTokenizerFactory getMaxTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    return new SmartTokenizerFactory(indexSettings, env, name, settings).setSmart(false);
  }

  public static SmartTokenizerFactory getSmartTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    return new SmartTokenizerFactory(indexSettings, env, name, settings).setSmart(true);
  }

  public SmartTokenizerFactory setSmart(boolean smart) {
    this.configuration.setUseSmart(smart);
    return this;
  }

  @Override
  public Tokenizer create() {
    return new SmartTokenizer(configuration);
  }
}
