package com.chachazhan.analyzer.smart.dic;

import org.apache.logging.log4j.Logger;
import com.chachazhan.analyzer.smart.config.Configuration;
import com.chachazhan.analyzer.smart.util.DictUtils;
import com.chachazhan.analyzer.smart.util.StringUtils;
import lombok.experimental.var;
import lombok.val;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.plugin.analysis.mc.AnalysisSmartPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

/**
 * @author jack
 * @date 27/12/2017
 * @time 11:05
 */
public class Dictionary {

  private static final Logger logger = ESLoggerFactory.getLogger(Dictionary.class.getName());

  private static Dictionary singleton;

  private DictSegment mainDict;

  private DictSegment surnameDict;

  private DictSegment quantifierDict;

  private DictSegment suffixDict;

  private DictSegment prepDict;

  private DictSegment stopDict;

  private Configuration configuration;

  private String dictFolder;

  private Dictionary(Configuration configuration) {
    this.configuration = configuration;
    this.dictFolder = configuration.getEnvironment().configFile().resolve(AnalysisSmartPlugin.PLUGIN_NAME).toAbsolutePath().toString();
  }

  public static void init(Configuration configuration) {
    if (singleton == null) {
      synchronized (Dictionary.class) {
        if (singleton == null) {
          singleton = new Dictionary(configuration);
          singleton.loadDicts();
        }
      }
    }
  }

  public static Dictionary getSingleton() {
    if (singleton == null) {
      throw new IllegalStateException("词典尚未初始化，请先调用init方法!");
    }
    return singleton;
  }

  private DictSegment newDict() {
    return new DictSegment((char) 0);
  }

  public void addWords(Collection<String> words) {
    words.stream()
      .filter(StringUtils::isNotBlank)
      .map(this::getChars)
      .forEach(singleton.mainDict::fillSegment);
  }

  public void stopWords(Collection<String> words) {
    words.stream()
      .filter(StringUtils::isNotBlank)
      .map(this::getChars)
      .forEach(singleton.stopDict::fillSegment);
  }

  public Hit matchInMainDict(char[] chars) {
    return singleton.mainDict.match(chars);
  }

  public Hit matchInMainDict(char[] chars, int begin, int length) {
    return singleton.mainDict.match(chars, begin, length);
  }

  public Hit matchInQuantifierDict(char[] chars, int begin, int length) {
    return singleton.quantifierDict.match(chars, begin, length);
  }

  public Hit matchWithHit(char[] chars, int currentIndex, Hit matchedHit) {
    val ds = matchedHit.getMatchedDictSegment();
    return ds.match(chars, currentIndex, 1, matchedHit);
  }

  public boolean isStopWord(char[] chars, int begin, int length) {
    return singleton.stopDict.match(chars, begin, length).isMatch();
  }

  private void loadDicts() {
    loadMainDict();
    loadSingleDict();
    loadQuantifierDict();
    loadSurnameDict();
    loadPrepositionDict();
    loadSuffixDict();
    loadStopWordDict();
    loadExtraDict();
    loadExtraStopWordDict();
  }

  private void loadMainDict() {
    this.mainDict = newDict();
    val path = PathUtils.get(dictFolder, DictUtils.DICT_FILENAME_MAIN);
    try {
      doload(path, mainDict);
    } catch (IOException e) {
      logger.error("加载主词典出错!", e);
    }
    logger.info("加载主词典完成");
  }

  private void loadExtraDict() {
    val path = PathUtils.get(dictFolder, DictUtils.DICT_FILENAME_EXTRA);
    try {
      doload(path, mainDict);
    } catch (IOException e) {
      logger.error("加载extra词典出错!", e);
    }
    logger.info("加载extra词典完成");
  }

  private void loadSingleDict() {
    val path = PathUtils.get(dictFolder, DictUtils.DICT_FILENAME_SINGLE);
    try (val reader = Files.newBufferedReader(path, DictUtils.UTF8)) {
      reader.lines()
        .filter(StringUtils::isNotBlank)
        .map(this::getChars)
        .forEach(mainDict::fillSegment);
    } catch (Exception e) {
      logger.error("加载单字词典出错!", e);
    }
    logger.info("加载单字词典完成");
  }

  private void loadStopWordDict() {
    loadEnglishStopWordDict();
    loadChineseStopWordDict();
  }

  private void loadExtraStopWordDict() {
    val path = PathUtils.get(dictFolder, DictUtils.DICT_FILENAME_EXTRA_STOP);
    try {
      doload(path, stopDict);
    } catch (IOException e) {
      logger.error("加载extra停用词典出错!", e);
    }
    logger.info("加载extra停用词典完成");
  }

  private void loadEnglishStopWordDict() {
    this.stopDict = newDict();
    val path = PathUtils.get(dictFolder, DictUtils.DICT_FILENAME_STOP_ENGLISH);
    try {
      doload(path, stopDict);
    } catch (IOException e) {
      logger.error("加载英文停用词典出错!", e);
    }
    logger.info("加载英文停用词典完成");
  }

  private void loadChineseStopWordDict() {
    val path = PathUtils.get(dictFolder, DictUtils.DICT_FILENAME_STOP_CHINESE);
    try {
      doload(path, stopDict);
    } catch (IOException e) {
      logger.error("加载中文停用词典出错!", e);
    }
    logger.info("加载中文停用词典完成");
  }

  private void loadQuantifierDict() {
    this.quantifierDict = newDict();
    val path = PathUtils.get(dictFolder, DictUtils.DICT_FILENAME_QUANTIFIER);
    try {
      doload(path, quantifierDict);
    } catch (IOException e) {
      logger.error("加载量词词典出错!", e);
    }
    logger.info("加载量词词典完成");
  }

  private void loadSurnameDict() {
    this.surnameDict = newDict();
    val path = PathUtils.get(dictFolder, DictUtils.DICT_FILENAME_SURNAME);
    try {
      doload(path, surnameDict);
    } catch (IOException e) {
      logger.error("加载surname词典出错!", e);
    }
    logger.info("加载surname词典完成");
  }

  private void loadSuffixDict() {
    this.suffixDict = newDict();
    val path = PathUtils.get(dictFolder, DictUtils.DICT_FILENAME_SUFFIX);
    try {
      doload(path, suffixDict);
    } catch (IOException e) {
      logger.error("加载suffix词典出错!", e);
    }
    logger.info("加载suffix词典完成");
  }

  private void loadPrepositionDict() {
    this.prepDict = newDict();
    val path = PathUtils.get(dictFolder, DictUtils.DICT_FILENAME_PREPOSITION);
    try {
      doload(path, prepDict);
    } catch (IOException e) {
      logger.error("加载前置词典出错!", e);
    }
    logger.info("加载前置词典完成");
  }

  private void doload(Path path, DictSegment dictSegment) throws IOException {
    try (val reader = Files.newBufferedReader(path, DictUtils.UTF8)) {
      reader.lines()
        .filter(StringUtils::isNotBlank)
        .map(this::getChars)
        .forEach(dictSegment::fillSegment);
    }
  }

  private char[] getChars(String word) {
    return configuration.isEnableLowercase() ? word.trim().toLowerCase().toCharArray() : word.trim().toCharArray();
  }

  public void reload() {
    logger.info("重新加载词典...");
    var tmpDict = new Dictionary(this.configuration);
    tmpDict.loadDicts();
    singleton = tmpDict;
    logger.info("重新加载词典完毕.");
  }

}
