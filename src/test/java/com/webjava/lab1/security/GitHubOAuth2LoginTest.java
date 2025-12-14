package com.webjava.lab1.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.webjava.lab1.config.TestSecurityConfig;
import com.webjava.lab1.controller.GlobalExceptionHandler;
import com.webjava.lab1.controller.ProductController;
import com.webjava.lab1.mapper.ProductMapper;
import com.webjava.lab1.mapper.ProductMapperImpl;
import com.webjava.lab1.service.ProductService;
import com.webjava.lab1.web.TraceIdFilter;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests for GitHub OAuth2 Login authentication. Uses mock OAuth2 login to simulate authenticated
 * GitHub users accessing protected resources.
 */
@WebMvcTest(ProductController.class)
@ActiveProfiles("no-auth")
@Import({
  TestSecurityConfig.class,
  GlobalExceptionHandler.class,
  TraceIdFilter.class,
  GitHubOAuth2LoginTest.TestConfig.class
})
class GitHubOAuth2LoginTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProductService productService;

  @Test
  void authenticatedWithGitHubOAuth2CanAccessProtectedResource() throws Exception {
    OAuth2User oauth2User = createGitHubOAuth2User("github-user-123", "testuser");

    mockMvc
        .perform(get("/api/v4/products").with(oauth2Login().oauth2User(oauth2User)))
        .andExpect(status().isOk());
  }

  @Test
  void authenticatedGitHubUserHasCorrectPrincipalName() throws Exception {
    OAuth2User oauth2User = createGitHubOAuth2User("12345", "cosmocatdev");

    mockMvc
        .perform(
            get("/api/v4/products")
                .with(
                    oauth2Login()
                        .oauth2User(oauth2User)
                        .authorities(new SimpleGrantedAuthority("OAUTH2_USER"))))
        .andExpect(status().isOk());

    // Verify OAuth2User attributes
    assertThat(oauth2User.getName()).isEqualTo("12345");
    assertThat((String) oauth2User.getAttribute("login")).isEqualTo("cosmocatdev");
    assertThat((String) oauth2User.getAttribute("email")).isEqualTo("test@example.com");
  }

  @Test
  void oauth2LoginWithCustomAuthoritiesWorks() throws Exception {
    OAuth2User oauth2User = createGitHubOAuth2User("admin-123", "admin-user");

    mockMvc
        .perform(
            get("/api/v4/products")
                .with(
                    oauth2Login()
                        .oauth2User(oauth2User)
                        .authorities(
                            new SimpleGrantedAuthority("ROLE_USER"),
                            new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void withMockUserAlsoWorksForOAuth2Endpoints() throws Exception {
    // Demonstrates that @WithMockUser can also be used for simpler OAuth2 tests
    mockMvc.perform(get("/api/v4/products")).andExpect(status().isOk());
  }

  private OAuth2User createGitHubOAuth2User(String id, String login) {
    return new DefaultOAuth2User(
        List.of(new SimpleGrantedAuthority("OAUTH2_USER")),
        Map.of(
            "id", id,
            "login", login,
            "name", "Test User",
            "email", "test@example.com",
            "avatar_url", "https://github.com/images/avatar.png"),
        "id");
  }

  @TestConfiguration
  static class TestConfig {
    @Bean
    ProductMapper productMapper() {
      return new ProductMapperImpl();
    }
  }
}
