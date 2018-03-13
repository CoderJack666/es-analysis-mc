package com.chachazhan.analyzer.smart.util;

/**
 * @author jack
 * @date 27/12/2017
 * @time 11:59
 */
public class StringUtils {

  public static boolean isBlank(String str) {
    return str == null || str.trim().length() == 0;
  }

  public static boolean isNotBlank(String str) {
    return !isBlank(str);
  }

}
