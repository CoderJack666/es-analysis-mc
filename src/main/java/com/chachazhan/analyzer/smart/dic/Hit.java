package com.chachazhan.analyzer.smart.dic;

import lombok.Data;

/**
 * @author jack
 * @date 27/12/2017
 * @time 19:15
 */
@Data
public class Hit {

  private static final int UNMATCH = 0x00000000;
  private static final int MATCH = 0x00000001;
  private static final int PREFIX = 0x00000010;

  private int hitStatus = UNMATCH;
  private DictSegment matchedDictSegment;
  private int begin;
  private int end;

  public boolean isMatch() {
    return (this.hitStatus & MATCH) > 0;
  }

  public void setMatch() {
    this.hitStatus = this.hitStatus | MATCH;
  }

  public boolean isPrefix() {
    return (this.hitStatus & PREFIX) > 0;
  }

  public void setPrefix() {
    this.hitStatus = this.hitStatus | PREFIX;
  }

  public boolean isUnmatch() {
    return this.hitStatus == UNMATCH;
  }

  public void setUnmatch() {
    this.hitStatus = UNMATCH;
  }

}
