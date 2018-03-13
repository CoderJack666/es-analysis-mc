package org.elasticsearch.index.analysis;

import com.chachazhan.analyzer.smart.lucene.CharacterAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * @author jack
 * @date 28/12/2017
 * @time 18:44
 */
public class CharacterAnalyzerProvider extends AbstractIndexAnalyzerProvider<CharacterAnalyzer> {

  private final CharacterAnalyzer characterAnalyzer;

  public CharacterAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    super(indexSettings, name, settings);
    this.characterAnalyzer = new CharacterAnalyzer();
  }

  public static CharacterAnalyzerProvider getCharacterAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    return new CharacterAnalyzerProvider(indexSettings, env, name, settings);
  }

  @Override
  public CharacterAnalyzer get() {
    return characterAnalyzer;
  }
}
