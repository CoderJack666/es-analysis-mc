package com.chachazhan.analyzer.smart.config;

import com.chachazhan.analyzer.smart.dic.Dictionary;
import lombok.Getter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

/**
 * @author jack
 * @date 27/12/2017
 * @time 10:57
 */
public class Configuration {

  private static final String TRUE = "true";

  @Getter
  private final Environment environment;
  @Getter
  private final Settings settings;

  @Getter
  private boolean useSmart;

  // 是否启用小写处理，默认:是
  @Getter
  private boolean enableLowercase;
  // 是否保留单字
  @Getter
  private boolean enableSingle;

  @Inject
  public Configuration(Environment environment, Settings settings) {
    this.environment = environment;
    this.settings = settings;

    this.useSmart = TRUE.equals(settings.get("use_smart", "true"));
    this.enableLowercase = TRUE.equals(settings.get("enable_lowercase", "true"));
    this.enableSingle = TRUE.equals(settings.get("enable_single", "false"));

    Dictionary.init(this);
  }

  public Configuration setUseSmart(boolean useSmart) {
    this.useSmart = useSmart;
    return this;
  }

}
