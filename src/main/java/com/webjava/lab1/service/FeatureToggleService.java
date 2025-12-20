package com.webjava.lab1.service;

import com.webjava.lab1.config.FeatureToggleProperties;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

  private final FeatureToggleProperties properties;

  public FeatureToggleService(FeatureToggleProperties properties) {
    this.properties = properties;
  }

  public boolean isFeatureEnabled(String featureName) {
    return properties.isEnabled(featureName);
  }

  public void checkFeature(String featureName) {
    if (!isFeatureEnabled(featureName)) {
      throw new FeatureNotAvailableException(featureName);
    }
  }
}
