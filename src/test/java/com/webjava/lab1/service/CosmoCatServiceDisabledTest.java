package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "feature.cosmoCats.enabled=false")
class CosmoCatServiceDisabledTest {

  @Autowired
  private CosmoCatService cosmoCatService;

  @Test
  void getCosmoCatsThrowsExceptionWhenFeatureDisabled() {
    assertThatThrownBy(() -> cosmoCatService.getCosmoCats())
        .isInstanceOf(FeatureNotAvailableException.class)
        .hasMessageContaining("cosmoCats");
  }
}
