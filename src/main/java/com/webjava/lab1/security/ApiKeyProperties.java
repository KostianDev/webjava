package com.webjava.lab1.security;

import java.util.ArrayList;
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
  private List<ApiKeyEntry> keys = new ArrayList<>();

  @Getter
  @Setter
  public static class ApiKeyEntry {
    private String key;
    private String name;
    private List<String> scopes = List.of("read", "write");
  }

  /**
   * Find an API key entry by key value.
   *
   * @param apiKey the API key to search for
   * @return the matching ApiKeyEntry or null if not found
   */
  public ApiKeyEntry findByKey(String apiKey) {
    return keys.stream().filter(entry -> entry.getKey().equals(apiKey)).findFirst().orElse(null);
  }
}
