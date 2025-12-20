package com.webjava.lab1.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "feature")
public class FeatureToggleProperties {

  private Map<String, Boolean> toggles = new HashMap<>();

  public Map<String, Boolean> getToggles() {
    return toggles;
  }

  public void setToggles(Map<String, Boolean> toggles) {
    this.toggles = toggles;
  }

  public boolean isEnabled(String featureName) {
    return toggles.getOrDefault(featureName, false);
  }
}
