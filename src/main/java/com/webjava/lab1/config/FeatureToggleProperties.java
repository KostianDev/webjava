package com.webjava.lab1.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "feature")
public class FeatureToggleProperties {

  private final Map<String, Boolean> toggles = new HashMap<>();

  public Map<String, Boolean> getToggles() {
    return toggles;
  }

  public boolean isCosmoCatsEnabled() {
    return toggles.getOrDefault("cosmoCats.enabled", false);
  }

  public boolean isKittyProductsEnabled() {
    return toggles.getOrDefault("kittyProducts.enabled", false);
  }

  public void setCosmoCats(Map<String, Boolean> cosmoCats) {
    if (cosmoCats.containsKey("enabled")) {
      toggles.put("cosmoCats.enabled", cosmoCats.get("enabled"));
    }
  }

  public void setKittyProducts(Map<String, Boolean> kittyProducts) {
    if (kittyProducts.containsKey("enabled")) {
      toggles.put("kittyProducts.enabled", kittyProducts.get("enabled"));
    }
  }
}
