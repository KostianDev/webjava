package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource(properties = "feature.cosmoCats.enabled=false")
class CosmoCatServiceDisabledTest {

  @Autowired private CosmoCatService cosmoCatService;

  @Test
  void getCosmoCatsThrowsExceptionWhenFeatureDisabled() {
    assertThatThrownBy(() -> cosmoCatService.getCosmoCats())
        .isInstanceOf(FeatureNotAvailableException.class)
        .hasMessageContaining("cosmoCats");
  }
}
