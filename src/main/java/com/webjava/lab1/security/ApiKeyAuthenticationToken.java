package com.webjava.lab1.security;

import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

  private final String apiKey;

  public ApiKeyAuthenticationToken(String apiKey) {
    super(
        List.of(
            new SimpleGrantedAuthority("SCOPE_read"), new SimpleGrantedAuthority("SCOPE_write")));
    this.apiKey = apiKey;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return apiKey;
  }

  @Override
  public Object getPrincipal() {
    return "api-key-user";
  }
}
