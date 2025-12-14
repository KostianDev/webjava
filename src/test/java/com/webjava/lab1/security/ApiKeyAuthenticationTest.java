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

    ApiKeyProperties.ApiKeyEntry entry1 = new ApiKeyProperties.ApiKeyEntry();
    entry1.setKey("key-1");
    entry1.setName("Client 1");
    entry1.setScopes(List.of("read", "write"));

    ApiKeyProperties.ApiKeyEntry entry2 = new ApiKeyProperties.ApiKeyEntry();
    entry2.setKey("key-2");
    entry2.setName("Client 2");
    entry2.setScopes(List.of("read"));

    properties.setKeys(List.of(entry1, entry2));

    assertThat(properties.isEnabled()).isTrue();
    assertThat(properties.getHeaderName()).isEqualTo("X-Custom-API-Key");
    assertThat(properties.getKeys()).hasSize(2);
  }

  @Test
  void apiKeyAuthenticationTokenHoldsCredentials() {
    List<String> scopes = List.of("read", "write", "admin");
    ApiKeyAuthenticationToken token =
        new ApiKeyAuthenticationToken("test-api-key", "Test Client", scopes);

    assertThat(token.getPrincipal()).isEqualTo("Test Client");
    assertThat(token.getCredentials()).isEqualTo("test-api-key");
    assertThat(token.isAuthenticated()).isTrue();
    assertThat(token.getAuthorities()).hasSize(3);
    assertThat(token.getAuthorities())
        .extracting("authority")
        .containsExactlyInAnyOrder("SCOPE_read", "SCOPE_write", "SCOPE_admin");
  }

  @Test
  void apiKeyAuthenticationTokenWithEmptyScopes() {
    ApiKeyAuthenticationToken token =
        new ApiKeyAuthenticationToken("test-api-key", "Minimal Client", List.of());

    assertThat(token.getPrincipal()).isEqualTo("Minimal Client");
    assertThat(token.getCredentials()).isEqualTo("test-api-key");
    assertThat(token.isAuthenticated()).isTrue();
    assertThat(token.getAuthorities()).isEmpty();
  }

  @Test
  void apiKeyPropertiesDefaultValues() {
    ApiKeyProperties properties = new ApiKeyProperties();

    assertThat(properties.isEnabled()).isFalse();
    assertThat(properties.getHeaderName()).isEqualTo("X-API-Key");
    assertThat(properties.getKeys()).isEmpty();
  }

  @Test
  void apiKeyPropertiesFindByKeyReturnsCorrectEntry() {
    ApiKeyProperties properties = new ApiKeyProperties();

    ApiKeyProperties.ApiKeyEntry prodEntry = new ApiKeyProperties.ApiKeyEntry();
    prodEntry.setKey("production-key");
    prodEntry.setName("Production Client");
    prodEntry.setScopes(List.of("read", "write"));

    ApiKeyProperties.ApiKeyEntry stagingEntry = new ApiKeyProperties.ApiKeyEntry();
    stagingEntry.setKey("staging-key");
    stagingEntry.setName("Staging Client");
    stagingEntry.setScopes(List.of("read"));

    properties.setKeys(List.of(prodEntry, stagingEntry));

    ApiKeyProperties.ApiKeyEntry found = properties.findByKey("production-key");
    assertThat(found).isNotNull();
    assertThat(found.getName()).isEqualTo("Production Client");
    assertThat(found.getScopes()).containsExactly("read", "write");

    ApiKeyProperties.ApiKeyEntry staging = properties.findByKey("staging-key");
    assertThat(staging).isNotNull();
    assertThat(staging.getName()).isEqualTo("Staging Client");

    ApiKeyProperties.ApiKeyEntry notFound = properties.findByKey("invalid-key");
    assertThat(notFound).isNull();
  }

  @Test
  void apiKeyEntryDefaultValues() {
    ApiKeyProperties.ApiKeyEntry entry = new ApiKeyProperties.ApiKeyEntry();

    assertThat(entry.getKey()).isNull();
    assertThat(entry.getName()).isNull();
    assertThat(entry.getScopes()).containsExactly("read", "write");
  }
}
