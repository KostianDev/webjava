package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.domain.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductServiceTest {

  private ProductService service;

  @BeforeEach
  void setUp() {
    service = new ProductService();
    service.init();
  }

  @Test
  void createAssignsIdAndPersistsProduct() {
    Product product =
        Product.builder()
            .name("Nebula Silk")
            .description("Shimmering fabric")
            .price(new BigDecimal("19.99"))
            .categoryName("Textiles")
            .build();

    Product created = service.create(product);

    assertThat(created.getId()).isNotNull();
    assertThat(service.list()).extracting(Product::getName).contains("Nebula Silk");
  }

  @Test
  void listReturnsInitialSampleData() {
    List<Product> products = service.list();

    assertThat(products).hasSize(2);
    products.clear();

    assertThat(service.list()).hasSize(2);
  }

  @Test
  void getReturnsEmptyWhenProductMissing() {
    assertThat(service.get(UUID.randomUUID())).isEmpty();
  }

  @Test
  void deleteIsIdempotentForMissingProduct() {
    List<Product> before = service.list();

    service.delete(UUID.randomUUID());

    assertThat(service.list()).hasSize(before.size());
  }
}
