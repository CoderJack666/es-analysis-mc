package org.elasticsearch.index.analysis;

import com.chachazhan.analyzer.smart.lucene.CharacterTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * @author jack
 * @date 28/12/2017
 * @time 18:40
 */
public class CharacterTokenizerFactory extends AbstractTokenizerFactory {

  public CharacterTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    super(indexSettings, name, settings);
  }

  @Inject
  public static CharacterTokenizerFactory getCharacterTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    return new CharacterTokenizerFactory(indexSettings, env, name, settings);
  }

  @Override
  public Tokenizer create() {
    return new CharacterTokenizer();
  }
}
