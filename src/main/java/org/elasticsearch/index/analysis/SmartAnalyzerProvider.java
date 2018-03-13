package org.elasticsearch.index.analysis;

import com.chachazhan.analyzer.smart.config.Configuration;
import com.chachazhan.analyzer.smart.lucene.SmartAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * @author jack
 * @date 27/12/2017
 * @time 10:55
 */
public class SmartAnalyzerProvider extends AbstractIndexAnalyzerProvider<SmartAnalyzer> {

  private final SmartAnalyzer smartAnalyzer;

  public SmartAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings, boolean useSmart) {
    super(indexSettings, name, settings);
    Configuration configuration = new Configuration(env, settings).setUseSmart(useSmart);
    this.smartAnalyzer = new SmartAnalyzer(configuration);
  }

  public static SmartAnalyzerProvider getSmartAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    return new SmartAnalyzerProvider(indexSettings, env, name, settings, true);
  }

  public static SmartAnalyzerProvider getMaxAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    return new SmartAnalyzerProvider(indexSettings, env, name, settings, false);
  }

  @Override
  public SmartAnalyzer get() {
    return smartAnalyzer;
  }
}
