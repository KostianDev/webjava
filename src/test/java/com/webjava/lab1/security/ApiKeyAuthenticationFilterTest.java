package com.webjava.lab1.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Tests for ApiKeyAuthenticationFilter. */
@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationFilterTest {

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  private ApiKeyProperties apiKeyProperties;
  private ApiKeyAuthenticationFilter filter;

  private HttpServletRequest getRequest() {
    return Objects.requireNonNull(request);
  }

  private HttpServletResponse getResponse() {
    return Objects.requireNonNull(response);
  }

  private FilterChain getFilterChain() {
    return Objects.requireNonNull(filterChain);
  }

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();

    apiKeyProperties = new ApiKeyProperties();
    apiKeyProperties.setEnabled(true);
    apiKeyProperties.setHeaderName("X-API-Key");

    ApiKeyProperties.ApiKeyEntry validEntry = new ApiKeyProperties.ApiKeyEntry();
    validEntry.setKey("valid-api-key");
    validEntry.setName("Test Client");
    validEntry.setScopes(List.of("read", "write"));

    apiKeyProperties.setKeys(List.of(validEntry));

    filter = new ApiKeyAuthenticationFilter(apiKeyProperties);
  }

  @Test
  void validApiKeySetsAuthentication() throws Exception {
    when(getRequest().getHeader("X-API-Key")).thenReturn("valid-api-key");

    filter.doFilterInternal(getRequest(), getResponse(), getFilterChain());

    verify(getFilterChain()).doFilter(getRequest(), getResponse());

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth).isInstanceOf(ApiKeyAuthenticationToken.class);
    assertThat(auth.getPrincipal()).isEqualTo("Test Client");
    assertThat(auth.getCredentials()).isEqualTo("valid-api-key");
    assertThat(auth.getAuthorities())
        .extracting("authority")
        .containsExactlyInAnyOrder("SCOPE_read", "SCOPE_write");
  }

  @Test
  void invalidApiKeyReturns401() throws Exception {
    when(getRequest().getHeader("X-API-Key")).thenReturn("invalid-api-key");
    when(getRequest().getRequestURI()).thenReturn("/api/v4/products");
    when(getRequest().getRemoteAddr()).thenReturn("127.0.0.1");

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(getResponse().getWriter()).thenReturn(printWriter);

    filter.doFilterInternal(getRequest(), getResponse(), getFilterChain());

    // Filter chain should NOT be called for invalid API key
    verify(getFilterChain(), never()).doFilter(any(), any());

    // Response should be 401
    verify(getResponse()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(getResponse()).setContentType("application/json");

    // Check response body
    printWriter.flush();
    String responseBody = stringWriter.toString();
    assertThat(responseBody).contains("Invalid API key");
    assertThat(responseBody).contains("Unauthorized");
  }

  @Test
  void noApiKeyHeaderContinuesFilterChain() throws Exception {
    when(getRequest().getHeader("X-API-Key")).thenReturn(null);

    filter.doFilterInternal(getRequest(), getResponse(), getFilterChain());

    // Filter chain should continue (JWT auth will handle it)
    verify(getFilterChain()).doFilter(getRequest(), getResponse());

    // No authentication set (JWT auth will handle it)
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNull();
  }

  @Test
  void emptyApiKeyHeaderContinuesFilterChain() throws Exception {
    when(getRequest().getHeader("X-API-Key")).thenReturn("   ");

    filter.doFilterInternal(getRequest(), getResponse(), getFilterChain());

    // Filter chain should continue (JWT auth will handle it)
    verify(getFilterChain()).doFilter(getRequest(), getResponse());

    // No authentication set
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNull();
  }

  @Test
  void disabledApiKeyContinuesFilterChain() throws Exception {
    apiKeyProperties.setEnabled(false);
    // Note: header is not stubbed because filter returns early when disabled

    filter.doFilterInternal(getRequest(), getResponse(), getFilterChain());

    verify(getFilterChain()).doFilter(getRequest(), getResponse());

    // No authentication set since API key is disabled
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNull();
  }

  @Test
  void customHeaderNameIsUsed() throws Exception {
    apiKeyProperties.setHeaderName("X-Custom-Key");
    filter = new ApiKeyAuthenticationFilter(apiKeyProperties);

    when(getRequest().getHeader("X-Custom-Key")).thenReturn("valid-api-key");

    filter.doFilterInternal(getRequest(), getResponse(), getFilterChain());

    verify(getFilterChain()).doFilter(getRequest(), getResponse());

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getPrincipal()).isEqualTo("Test Client");
  }

  @Test
  void existingAuthenticationIsNotOverwritten() throws Exception {
    // Set existing authentication
    ApiKeyAuthenticationToken existingAuth =
        new ApiKeyAuthenticationToken("existing-key", "Existing Client", List.of("admin"));
    SecurityContextHolder.getContext().setAuthentication(existingAuth);

    when(getRequest().getHeader("X-API-Key")).thenReturn("valid-api-key");

    filter.doFilterInternal(getRequest(), getResponse(), getFilterChain());

    verify(getFilterChain()).doFilter(getRequest(), getResponse());

    // Authentication should remain unchanged
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isSameAs(existingAuth);
  }
}
