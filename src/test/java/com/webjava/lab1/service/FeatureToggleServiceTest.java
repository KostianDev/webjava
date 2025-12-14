package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.config.FeatureToggleProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(
    properties = {"feature.cosmoCats.enabled=true", "feature.kittyProducts.enabled=false"})
class FeatureToggleServiceTest extends AbstractIntegrationTest {

  @Autowired private FeatureToggleService featureToggleService;

  @Autowired private FeatureToggleProperties featureToggleProperties;

  @Test
  void isFeatureEnabledReturnsTrueForEnabledFeature() {
    assertThat(featureToggleService.isFeatureEnabled("cosmoCats")).isTrue();
  }

  @Test
  void isFeatureEnabledReturnsFalseForDisabledFeature() {
    assertThat(featureToggleService.isFeatureEnabled("kittyProducts")).isFalse();
  }

  @Test
  void isFeatureEnabledReturnsFalseForUnknownFeature() {
    assertThat(featureToggleService.isFeatureEnabled("unknownFeature")).isFalse();
  }

  @Test
  void checkFeatureDoesNotThrowWhenEnabled() {
    assertThatCode(() -> featureToggleService.checkFeature("cosmoCats")).doesNotThrowAnyException();
  }

  @Test
  void checkFeatureThrowsWhenDisabled() {
    assertThatThrownBy(() -> featureToggleService.checkFeature("kittyProducts"))
        .isInstanceOf(FeatureNotAvailableException.class)
        .hasMessageContaining("kittyProducts");
  }

  @Test
  void propertiesAreLoadedCorrectly() {
    assertThat(featureToggleProperties.isCosmoCatsEnabled()).isTrue();
    assertThat(featureToggleProperties.isKittyProductsEnabled()).isFalse();
  }

  @Test
  void featureNotAvailableExceptionContainsFeatureName() {
    FeatureNotAvailableException ex = new FeatureNotAvailableException("testFeature");

    assertThat(ex.getFeatureName()).isEqualTo("testFeature");
    assertThat(ex.getMessage()).contains("testFeature");
  }
}
