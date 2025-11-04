package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    Product product = new Product(
      null,
      "Nebula Silk",
      "Shimmering fabric",
      new BigDecimal("19.99"),
      "Textiles"
    );

    Product created = service.create(product);

    assertThat(created.getId()).isNotNull();
    assertThat(service.get(created.getId())).contains(created);
    assertThat(service.list())
      .extracting(Product::getName)
      .contains("Nebula Silk");
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
  void updateReplacesExistingProduct() {
    Product existing = service.list().get(0);
    UUID id = existing.getId();
    Product updateRequest = new Product(
      null,
      "Updated",
      "Updated description",
      new BigDecimal("1.11"),
      "Updated"
    );

    Product updated = service.update(id, updateRequest);

    assertThat(updated.getId()).isEqualTo(id);
    assertThat(updated.getName()).isEqualTo("Updated");
    assertThat(service.get(id)).contains(updated);
  }

  @Test
  void updateThrowsWhenProductMissing() {
    Product updateRequest = new Product(
      null,
      "Missing",
      "Missing",
      new BigDecimal("2.22"),
      "Nowhere"
    );

    assertThatThrownBy(() ->
      service.update(UUID.randomUUID(), updateRequest)
    ).isInstanceOf(ProductNotFoundException.class);
  }

  @Test
  void deleteRemovesExistingProduct() {
    Product existing = service.list().get(0);
    UUID id = existing.getId();

    service.delete(id);

    assertThat(service.get(id)).isEmpty();
    assertThat(service.list()).hasSize(1);
  }

  @Test
  void deleteIsIdempotentForMissingProduct() {
    List<Product> before = service.list();

    service.delete(UUID.randomUUID());

    assertThat(service.list()).hasSize(before.size());
  }
}
