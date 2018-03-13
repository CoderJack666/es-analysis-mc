package com.chachazhan.analyzer.smart.core;

import com.chachazhan.analyzer.smart.config.Configuration;
import lombok.val;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jack
 * @date 27/12/2017
 * @time 21:21
 */
public final class SmartSegmenter {

  private Reader input;
  private AnalyzeContext context;
  private List<Segmenter> segmenters;
  private Arbitrator arbitrator;
  private Configuration configuration;

  public SmartSegmenter(Reader input, Configuration configuration) {
    this.input = input;
    this.configuration = configuration;
    this.init();
  }

  private void init() {
    this.context = new AnalyzeContext(this.configuration);
    this.segmenters = this.loadSegmenters();
    this.arbitrator = new Arbitrator();
  }

  private List<Segmenter> loadSegmenters() {
    val segmenters = new ArrayList<Segmenter>(4);
    segmenters.add(new LetterSegmenter());
    segmenters.add(new CNQuantifierSegmenter());
    segmenters.add(new CJKSegmenter());
    return segmenters;
  }

  public synchronized Lexeme next() throws IOException {
    Lexeme lexeme;
    while ((lexeme = context.getNextLexeme()) == null) {
      val available = context.fillBuffer(this.input);
      if (available <= 0) {
        context.reset();
        return null;
      } else {
        context.initCursor();
        do {
          segmenters.forEach(segmenter -> segmenter.analyze(context));
          if (context.needRefillBuffer()) {
            break;
          }
        } while (context.moveCursor());

        segmenters.forEach(Segmenter::reset);
      }
      this.arbitrator.process(context);
      context.outputToResult();
      context.markBufferOffset();
    }
    return lexeme;
  }

  public synchronized void reset(Reader input) {
    this.input = input;
    this.context.reset();
    this.segmenters.forEach(Segmenter::reset);
  }

}
