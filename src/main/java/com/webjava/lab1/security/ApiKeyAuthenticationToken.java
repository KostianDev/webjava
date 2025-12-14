package com.webjava.lab1.security;

import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

  private final String apiKey;
  private final String clientName;

  public ApiKeyAuthenticationToken(String apiKey, String clientName, List<String> scopes) {
    super(scopes.stream().map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope)).toList());
    this.apiKey = apiKey;
    this.clientName = clientName;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return apiKey;
  }

  @Override
  public Object getPrincipal() {
    return clientName;
  }
}
