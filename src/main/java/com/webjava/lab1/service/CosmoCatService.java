package com.webjava.lab1.service;

import com.webjava.lab1.config.FeatureToggle;
import com.webjava.lab1.domain.CosmoCat;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CosmoCatService {

  private final List<CosmoCat> cosmoCats = new ArrayList<>();

  @PostConstruct
  public void init() {
    cosmoCats.add(new CosmoCat(UUID.randomUUID(), "Nebula Whiskers", "Mars", 9001));
    cosmoCats.add(new CosmoCat(UUID.randomUUID(), "Stellar Paws", "Jupiter", 7500));
    cosmoCats.add(new CosmoCat(UUID.randomUUID(), "Galaxy Fluff", "Saturn", 8200));
  }

  @FeatureToggle("cosmoCats")
  public List<CosmoCat> getCosmoCats() {
    return new ArrayList<>(cosmoCats);
  }
}
