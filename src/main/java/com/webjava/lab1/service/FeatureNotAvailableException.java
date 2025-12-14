package com.webjava.lab1.service;

public class FeatureNotAvailableException extends RuntimeException {

  private final String featureName;

  public FeatureNotAvailableException(String featureName) {
    super("Feature '" + featureName + "' is not available");
    this.featureName = featureName;
  }

  public String getFeatureName() {
    return featureName;
  }
}
