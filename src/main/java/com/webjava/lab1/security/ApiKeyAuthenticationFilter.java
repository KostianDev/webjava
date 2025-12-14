package com.webjava.lab1.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

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

    if (apiKey != null && apiKeyProperties.getValidKeys().contains(apiKey)) {
      ApiKeyAuthenticationToken authentication = new ApiKeyAuthenticationToken(apiKey);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }
}
