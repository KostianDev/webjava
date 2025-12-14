package com.webjava.lab1.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for API Key authentication components. */
class ApiKeyAuthenticationTest {

  @Test
  void apiKeyPropertiesConfigurationWorks() {
    ApiKeyProperties properties = new ApiKeyProperties();

    properties.setEnabled(true);
    properties.setHeaderName("X-Custom-API-Key");
    properties.setValidKeys(List.of("key-1", "key-2", "key-3"));

    assertThat(properties.isEnabled()).isTrue();
    assertThat(properties.getHeaderName()).isEqualTo("X-Custom-API-Key");
    assertThat(properties.getValidKeys()).hasSize(3);
    assertThat(properties.getValidKeys()).containsExactly("key-1", "key-2", "key-3");
  }

  @Test
  void apiKeyAuthenticationTokenHoldsCredentials() {
    ApiKeyAuthenticationToken token = new ApiKeyAuthenticationToken("test-api-key");

    assertThat(token.getPrincipal()).isEqualTo("api-key-user");
    assertThat(token.getCredentials()).isEqualTo("test-api-key");
    assertThat(token.isAuthenticated()).isTrue();
    assertThat(token.getAuthorities()).hasSize(2);
    assertThat(token.getAuthorities())
        .extracting("authority")
        .containsExactlyInAnyOrder("SCOPE_read", "SCOPE_write");
  }

  @Test
  void apiKeyPropertiesDefaultValues() {
    ApiKeyProperties properties = new ApiKeyProperties();

    assertThat(properties.isEnabled()).isFalse();
    assertThat(properties.getHeaderName()).isEqualTo("X-API-Key");
    assertThat(properties.getValidKeys()).isEmpty();
  }

  @Test
  void apiKeyPropertiesWithMultipleKeys() {
    ApiKeyProperties properties = new ApiKeyProperties();

    properties.setValidKeys(List.of("production-key", "staging-key", "development-key"));

    assertThat(properties.getValidKeys()).hasSize(3);
    assertThat(properties.getValidKeys())
        .containsExactly("production-key", "staging-key", "development-key");
  }
}
