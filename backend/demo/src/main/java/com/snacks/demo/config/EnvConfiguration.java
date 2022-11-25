package com.snacks.demo.config;

import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = { "classpath:env.properties" }, ignoreResourceNotFound = false)
public class EnvConfiguration implements EnvironmentAware {

  private static Environment env;

  public static String getProperty(String key) {
    return env.getProperty(key);
  }

  @Override
  public void setEnvironment(Environment env) {
    EnvConfiguration.env = env;
  }
}