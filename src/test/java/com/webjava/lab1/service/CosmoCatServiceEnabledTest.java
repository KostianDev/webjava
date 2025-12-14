package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.domain.CosmoCat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "feature.cosmoCats.enabled=true")
class CosmoCatServiceEnabledTest extends AbstractIntegrationTest {

  @Autowired private CosmoCatService cosmoCatService;

  @Test
  void getCosmoCatsReturnsListWhenFeatureEnabled() {
    List<CosmoCat> cats = cosmoCatService.getCosmoCats();

    assertThat(cats).isNotEmpty();
    assertThat(cats).extracting(CosmoCat::getName).contains("Nebula Whiskers");
  }

  @Test
  void getCosmoCatsReturnsDefensiveCopy() {
    List<CosmoCat> cats = cosmoCatService.getCosmoCats();
    int originalSize = cats.size();

    cats.clear();

    assertThat(cosmoCatService.getCosmoCats()).hasSize(originalSize);
  }
}
