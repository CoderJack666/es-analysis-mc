package org.elasticsearch.index.analysis;

import com.chachazhan.analyzer.smart.lucene.DelimiterAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * @author jack
 * @date 28/12/2017
 * @time 21:26
 */
public class DelimiterAnalyzerProvider extends AbstractIndexAnalyzerProvider<DelimiterAnalyzer> {

  private final DelimiterAnalyzer delimiterAnalyzer;

  public DelimiterAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    super(indexSettings, name, settings);
    this.delimiterAnalyzer = new DelimiterAnalyzer('|');
  }

  public static DelimiterAnalyzerProvider getDelimiterAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    return new DelimiterAnalyzerProvider(indexSettings, env, name, settings);
  }

  @Override
  public DelimiterAnalyzer get() {
    return delimiterAnalyzer;
  }
}
