package com.chachazhan.analyzer.smart.core;

/**
 * 子分析器接口
 *
 * @author jack
 * @date 27/12/2017
 * @time 20:24
 */
public interface Segmenter {

  /**
   * 从分析器读取下一个可能分解的词元对象。
   *
   * @param context 分词算法上下文
   */
  void analyze(AnalyzeContext context);

  /**
   * 重置子分析器状态
   */
  void reset();

}
