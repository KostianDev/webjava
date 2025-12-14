package com.webjava.lab1.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.webjava.lab1.config.TestSecurityConfig;
import com.webjava.lab1.controller.GlobalExceptionHandler;
import com.webjava.lab1.controller.ProductController;
import com.webjava.lab1.domain.Product;
import com.webjava.lab1.mapper.ProductMapper;
import com.webjava.lab1.mapper.ProductMapperImpl;
import com.webjava.lab1.service.ProductService;
import com.webjava.lab1.web.TraceIdFilter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests for JWT authentication using @WithMockUser to simulate authenticated requests. Uses no-auth
 * profile with TestSecurityConfig to allow testing authorization rules.
 */
@WebMvcTest(ProductController.class)
@ActiveProfiles("no-auth")
@Import({
  GlobalExceptionHandler.class,
  TraceIdFilter.class,
  TestSecurityConfig.class,
  JwtAuthenticationTest.TestConfig.class
})
class JwtAuthenticationTest {

  private static final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProductService productService;

  @BeforeEach
  void setUp() {
    Product product =
        Product.builder()
            .id(1L)
            .name("Test Product")
            .description("Test Description")
            .price(new BigDecimal("9.99"))
            .categoryName("Test Category")
            .build();

    when(productService.list()).thenReturn(List.of(product));
    when(productService.get(any(UUID.class))).thenReturn(Optional.of(product));
    when(productService.create(any(Product.class))).thenReturn(product);
  }

  @Test
  @WithMockUser(authorities = {"SCOPE_read"})
  void authenticatedWithReadScopeCanAccessGetProducts() throws Exception {
    mockMvc.perform(get("/api/v4/products")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"SCOPE_read"})
  void authenticatedWithReadScopeCanGetSingleProduct() throws Exception {
    mockMvc.perform(get("/api/v4/products/" + PRODUCT_ID)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"SCOPE_write"})
  void authenticatedWithWriteScopeCanPost() throws Exception {
    String payload =
        """
        {
          "name": "Star Test Product",
          "description": "Test Description",
          "price": 9.99,
          "category": "Test Category"
        }
        """;

    mockMvc
        .perform(
            post("/api/v4/products")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(payload))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(authorities = {"SCOPE_write"})
  void authenticatedWithWriteScopeCanDelete() throws Exception {
    mockMvc.perform(delete("/api/v4/products/" + PRODUCT_ID)).andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(authorities = {"SCOPE_read", "SCOPE_write"})
  void authenticatedWithBothScopesCanAccessAll() throws Exception {
    mockMvc.perform(get("/api/v4/products")).andExpect(status().isOk());

    String payload =
        """
        {
          "name": "Star Test Product",
          "description": "Test Description",
          "price": 9.99,
          "category": "Test Category"
        }
        """;

    mockMvc
        .perform(
            post("/api/v4/products")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(payload))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void authenticatedUserWithRoleCanAccess() throws Exception {
    // Testing that role-based access also works
    mockMvc.perform(get("/api/v4/products")).andExpect(status().isOk());
  }

  @TestConfiguration
  static class TestConfig {
    @Bean
    ProductMapper productMapper() {
      return new ProductMapperImpl();
    }
  }
}
