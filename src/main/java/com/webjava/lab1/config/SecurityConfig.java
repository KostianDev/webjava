package com.webjava.lab1.config;

import com.webjava.lab1.security.ApiKeyAuthenticationFilter;
import com.webjava.lab1.security.ApiKeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!no-auth")
public class SecurityConfig {

  private final ApiKeyProperties apiKeyProperties;

  public SecurityConfig(ApiKeyProperties apiKeyProperties) {
    this.apiKeyProperties = apiKeyProperties;
  }

  @Bean
  @Order(1)
  public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**")
        .csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authz ->
                authz
                    // Read operations require read scope
                    .requestMatchers(HttpMethod.GET, "/api/**")
                    .hasAuthority("SCOPE_read")
                    // Write operations require write scope
                    .requestMatchers(HttpMethod.POST, "/api/**")
                    .hasAuthority("SCOPE_write")
                    .requestMatchers(HttpMethod.PUT, "/api/**")
                    .hasAuthority("SCOPE_write")
                    .requestMatchers(HttpMethod.DELETE, "/api/**")
                    .hasAuthority("SCOPE_write")
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .addFilterBefore(
            new ApiKeyAuthenticationFilter(apiKeyProperties),
            BearerTokenAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/**")
        .authorizeHttpRequests(
            authz ->
                authz
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    .requestMatchers(
                        "/swagger-ui/**", "/v3/api-docs/**", "/cosmo-cats-product-api.yml")
                    .permitAll()
                    .requestMatchers("/error", "/login/**", "/oauth2/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2Login(Customizer.withDefaults());

    return http.build();
  }
}
