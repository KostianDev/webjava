package com.webjava.lab1.controller;

import static com.webjava.lab1.web.TraceIdFilter.TRACE_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webjava.lab1.domain.Product;
import com.webjava.lab1.dto.ProductDTO;
import com.webjava.lab1.mapper.ProductMapper;
import com.webjava.lab1.mapper.ProductMapperImpl;
import com.webjava.lab1.service.ProductNotFoundException;
import com.webjava.lab1.service.ProductService;
import com.webjava.lab1.web.TraceIdFilter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@Import(
  {
    GlobalExceptionHandler.class,
    TraceIdFilter.class,
    ProductControllerTest.TestConfig.class,
  }
)
class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProductService productService;

  @AfterEach
  void resetMocks() {
    Mockito.reset(productService);
  }

  @Test
  void createProductHappyPath() throws Exception {
    UUID id = UUID.randomUUID();
    Product created = new Product(
      id,
      "Star Yarn",
      "Antigravity",
      new BigDecimal("9.99"),
      "Textiles"
    );
    when(productService.create(any(Product.class))).thenReturn(created);

    ProductDTO requestDto = new ProductDTO(
      null,
      "Star Yarn",
      "Antigravity",
      new BigDecimal("9.99"),
      "Textiles"
    );
    String payload = Objects.requireNonNull(
      objectMapper.writeValueAsString(requestDto)
    );

    mockMvc
      .perform(
        post("/api/v1.1/products")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(payload)
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.name").value("Star Yarn"));

    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productService).create(captor.capture());
    Product persisted = captor.getValue();
    assertThat(persisted.getName()).isEqualTo("Star Yarn");
  }

  @Test
  void createProductValidationError() throws Exception {
    ProductDTO requestDto = new ProductDTO(
      null,
      "",
      "No name",
      new BigDecimal("0.00"),
      ""
    );
    String payload = Objects.requireNonNull(
      objectMapper.writeValueAsString(requestDto)
    );

    mockMvc
      .perform(
        post("/api/v1.1/products")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(payload)
      )
      .andExpect(status().isBadRequest())
      .andExpect(header().exists(TRACE_ID_HEADER))
      .andExpect(jsonPath("$.title").value("Validation Failed"))
      .andExpect(jsonPath("$.violations").isArray());
  }

  @Test
  void updateProductNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    when(productService.update(eq(id), any(Product.class))).thenThrow(
      new ProductNotFoundException(id)
    );

    ProductDTO requestDto = new ProductDTO(
      null,
      "Galaxy Silk",
      "",
      new BigDecimal("1.23"),
      "Textiles"
    );
    String payload = Objects.requireNonNull(
      objectMapper.writeValueAsString(requestDto)
    );

    mockMvc
      .perform(
        put("/api/v1.1/products/{id}", id)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(payload)
      )
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.title").value("Resource Not Found"));

    verify(productService).update(eq(id), any(Product.class));
  }

  @Test
  void getProductReturnsDtoWhenFound() throws Exception {
    UUID id = UUID.randomUUID();
    Product product = new Product(
      id,
      "Galaxy Milk",
      "Fresh",
      new BigDecimal("4.50"),
      "Food"
    );
    when(productService.get(id)).thenReturn(Optional.of(product));

    mockMvc
      .perform(get("/api/v1.1/products/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Galaxy Milk"))
      .andExpect(jsonPath("$.price").value(4.50));

    verify(productService).get(id);
  }

  @Test
  void listProductsReturnsDtoCollection() throws Exception {
    UUID id = UUID.randomUUID();
    Product product = new Product(
      id,
      "Star Juice",
      "",
      new BigDecimal("2.50"),
      "Drinks"
    );
    when(productService.list()).thenReturn(List.of(product));

    mockMvc
      .perform(get("/api/v1.1/products"))
      .andExpect(status().isOk())
      .andExpect(header().exists(TRACE_ID_HEADER))
      .andExpect(jsonPath("$[0].id").value(id.toString()));

    verify(productService).list();
  }

  @Test
  void updateProductHappyPath() throws Exception {
    UUID id = UUID.randomUUID();
    Product updated = new Product(
      id,
      "Star Shield",
      "",
      new BigDecimal("3.33"),
      "Defense"
    );
    when(productService.update(eq(id), any(Product.class))).thenReturn(updated);

    ProductDTO requestDto = new ProductDTO(
      null,
      "Star Shield",
      "",
      new BigDecimal("3.33"),
      "Defense"
    );
    String payload = Objects.requireNonNull(
      objectMapper.writeValueAsString(requestDto)
    );

    mockMvc
      .perform(
        put("/api/v1.1/products/{id}", id)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(payload)
      )
      .andExpect(status().isOk())
      .andExpect(header().exists(TRACE_ID_HEADER))
      .andExpect(jsonPath("$.name").value("Star Shield"));

    verify(productService).update(eq(id), any(Product.class));
  }

  @Test
  void deleteProductReturnsNoContent() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc
      .perform(delete("/api/v1.1/products/{id}", id))
      .andExpect(status().isNoContent())
      .andExpect(header().exists(TRACE_ID_HEADER));

    verify(productService).delete(id);
  }

  @Test
  void getProductHandlesUnexpectedException() throws Exception {
    UUID id = UUID.randomUUID();
    when(productService.get(id)).thenThrow(new IllegalStateException("kaboom"));

    mockMvc
      .perform(get("/api/v1.1/products/{id}", id))
      .andExpect(status().isInternalServerError())
      .andExpect(header().exists(TRACE_ID_HEADER))
      .andExpect(jsonPath("$.title").value("Internal Server Error"))
      .andExpect(jsonPath("$.path").value("/api/v1.1/products/" + id));

    verify(productService).get(id);
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    ProductMapper productMapper() {
      return new ProductMapperImpl();
    }

    @Bean
    ProductService productService() {
      return Mockito.mock(ProductService.class);
    }
  }
}
