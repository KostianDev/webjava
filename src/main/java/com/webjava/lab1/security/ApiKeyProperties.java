package com.webjava.lab1.security;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.api-key")
@Getter
@Setter
public class ApiKeyProperties {

  private boolean enabled = false;
  private String headerName = "X-API-Key";
  private List<String> validKeys = List.of();
}
