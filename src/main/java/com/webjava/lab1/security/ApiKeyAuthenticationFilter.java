package com.webjava.lab1.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

  private final ApiKeyProperties apiKeyProperties;

  public ApiKeyAuthenticationFilter(ApiKeyProperties apiKeyProperties) {
    this.apiKeyProperties = apiKeyProperties;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    if (!apiKeyProperties.isEnabled()) {
      filterChain.doFilter(request, response);
      return;
    }

    String apiKey = request.getHeader(apiKeyProperties.getHeaderName());

    // If no API key header provided, continue to next filter (JWT will handle it)
    if (apiKey == null || apiKey.isBlank()) {
      filterChain.doFilter(request, response);
      return;
    }

    // API key was provided - validate it
    ApiKeyProperties.ApiKeyEntry keyEntry = apiKeyProperties.findByKey(apiKey);

    if (keyEntry == null) {
      log.warn(
          "Invalid API key attempt from IP: {}, path: {}",
          request.getRemoteAddr(),
          request.getRequestURI());
      sendUnauthorizedResponse(response, "Invalid API key");
      return;
    }

    // Skip if already authenticated (e.g., by JWT)
    if (SecurityContextHolder.getContext().getAuthentication() != null
        && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
      filterChain.doFilter(request, response);
      return;
    }

    // Valid API key - authenticate
    ApiKeyAuthenticationToken authentication =
        new ApiKeyAuthenticationToken(apiKey, keyEntry.getName(), keyEntry.getScopes());
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.debug("Authenticated client '{}' via API key", keyEntry.getName());

    filterChain.doFilter(request, response);
  }

  private void sendUnauthorizedResponse(HttpServletResponse response, String message)
      throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
  }
}
