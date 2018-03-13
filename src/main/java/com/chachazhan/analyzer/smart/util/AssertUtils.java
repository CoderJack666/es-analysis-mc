package com.chachazhan.analyzer.smart.util;

/**
 * @author jack
 * @date 27/12/2017
 * @time 12:58
 */
public class AssertUtils {

  public static void notNull(Object object, String message) {
    if (object == null) {
      throw new NullPointerException(message);
    }
  }

}
