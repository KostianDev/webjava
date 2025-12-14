package com.webjava.lab1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!no-auth")
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authz ->
                authz
                    // Public endpoints
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    .requestMatchers(
                        "/swagger-ui/**", "/v3/api-docs/**", "/cosmo-cats-product-api.yml")
                    .permitAll()
                    .requestMatchers("/error")
                    .permitAll()
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
                    // All other requests require authentication
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

    return http.build();
  }
}
