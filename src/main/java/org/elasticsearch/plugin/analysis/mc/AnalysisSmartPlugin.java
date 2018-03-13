package org.elasticsearch.plugin.analysis.mc;

import lombok.val;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.index.analysis.*;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jack
 * @date 27/12/2017
 * @time 22:08
 */
public class AnalysisSmartPlugin extends Plugin implements AnalysisPlugin {

  public static String PLUGIN_NAME = "analysis-mc";

  @Override
  public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
    val extra = new HashMap<String, AnalysisModule.AnalysisProvider<TokenizerFactory>>();
    extra.put("mc-smart", SmartTokenizerFactory::getSmartTokenizerFactory);
    extra.put("mc-max", SmartTokenizerFactory::getMaxTokenizerFactory);
    extra.put("mc-chars", CharacterTokenizerFactory::getCharacterTokenizerFactory);
    extra.put("mc-delimiter", DelimiterTokenizerFactory::getDelimiterTokenizerFactory);
    return extra;
  }

  @Override
  public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
    val extra = new HashMap<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>>();
    extra.put("mc-smart", SmartAnalyzerProvider::getSmartAnalyzerProvider);
    extra.put("mc-max", SmartAnalyzerProvider::getMaxAnalyzerProvider);
    extra.put("mc-chars", CharacterAnalyzerProvider::getCharacterAnalyzerProvider);
    extra.put("mc-delimiter", DelimiterAnalyzerProvider::getDelimiterAnalyzerProvider);
    return extra;
  }
}
