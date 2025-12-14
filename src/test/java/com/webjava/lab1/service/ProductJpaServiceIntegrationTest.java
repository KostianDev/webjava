package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.entity.CategoryEntity;
import com.webjava.lab1.entity.ProductEntity;
import com.webjava.lab1.projection.ProductSummary;
import com.webjava.lab1.repository.CategoryRepository;
import com.webjava.lab1.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("null")
class ProductJpaServiceIntegrationTest extends AbstractIntegrationTest {

  @Autowired private ProductJpaService productJpaService;
  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;

  private CategoryEntity category;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
    categoryRepository.deleteAll();

    category =
        categoryRepository.save(
            CategoryEntity.builder().name("Space Gear").description("Equipment for space").build());
  }

  @Test
  @DisplayName("Should create product")
  void shouldCreateProduct() {
    ProductEntity product =
        ProductEntity.builder()
            .name("Space Helmet")
            .description("Protective helmet")
            .price(BigDecimal.valueOf(299.99))
            .build();

    ProductEntity saved = productJpaService.create(product, category.getId());

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("Space Helmet");
    assertThat(saved.getCategory().getName()).isEqualTo("Space Gear");
  }

  @Test
  @DisplayName("Should throw when creating product with non-existent category")
  void shouldThrowWhenCategoryNotFound() {
    ProductEntity product =
        ProductEntity.builder().name("Test Product").price(BigDecimal.valueOf(100)).build();

    assertThatThrownBy(() -> productJpaService.create(product, 999L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Category")
        .hasMessageContaining("999");
  }

  @Test
  @DisplayName("Should find all products")
  void shouldFindAllProducts() {
    productJpaService.create(
        ProductEntity.builder().name("Product 1").price(BigDecimal.valueOf(100)).build(),
        category.getId());
    productJpaService.create(
        ProductEntity.builder().name("Product 2").price(BigDecimal.valueOf(200)).build(),
        category.getId());

    assertThat(productJpaService.findAll()).hasSize(2);
  }

  @Test
  @DisplayName("Should find product by id")
  void shouldFindProductById() {
    ProductEntity saved =
        productJpaService.create(
            ProductEntity.builder().name("Unique Product").price(BigDecimal.valueOf(150)).build(),
            category.getId());

    assertThat(productJpaService.findById(saved.getId()))
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p.getName()).isEqualTo("Unique Product"));
  }

  @Test
  @DisplayName("Should return empty when product not found")
  void shouldReturnEmptyWhenNotFound() {
    assertThat(productJpaService.findById(999L)).isEmpty();
  }

  @Test
  @DisplayName("Should find products by category")
  void shouldFindProductsByCategory() {
    CategoryEntity otherCategory =
        categoryRepository.save(CategoryEntity.builder().name("Other Category").build());

    productJpaService.create(
        ProductEntity.builder().name("In Category").price(BigDecimal.valueOf(100)).build(),
        category.getId());
    productJpaService.create(
        ProductEntity.builder().name("Other").price(BigDecimal.valueOf(100)).build(),
        otherCategory.getId());

    List<ProductEntity> products = productJpaService.findByCategory(category.getId());

    assertThat(products).hasSize(1);
    assertThat(products.get(0).getName()).isEqualTo("In Category");
  }

  @Test
  @DisplayName("Should search products by name")
  void shouldSearchProductsByName() {
    productJpaService.create(
        ProductEntity.builder().name("Space Laser").price(BigDecimal.valueOf(500)).build(),
        category.getId());
    productJpaService.create(
        ProductEntity.builder().name("Space Suit").price(BigDecimal.valueOf(300)).build(),
        category.getId());
    productJpaService.create(
        ProductEntity.builder().name("Helmet").price(BigDecimal.valueOf(100)).build(),
        category.getId());

    List<ProductEntity> results = productJpaService.searchByName("space");

    assertThat(results).hasSize(2);
    assertThat(results)
        .extracting(ProductEntity::getName)
        .containsExactlyInAnyOrder("Space Laser", "Space Suit");
  }

  @Test
  @DisplayName("Should find products by price range")
  void shouldFindProductsByPriceRange() {
    productJpaService.create(
        ProductEntity.builder().name("Cheap").price(BigDecimal.valueOf(50)).build(),
        category.getId());
    productJpaService.create(
        ProductEntity.builder().name("Medium").price(BigDecimal.valueOf(150)).build(),
        category.getId());
    productJpaService.create(
        ProductEntity.builder().name("Expensive").price(BigDecimal.valueOf(500)).build(),
        category.getId());

    List<ProductEntity> results =
        productJpaService.findByPriceRange(BigDecimal.valueOf(100), BigDecimal.valueOf(200));

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Medium");
  }

  @Test
  @DisplayName("Should find affordable products using projection")
  void shouldFindAffordableProducts() {
    productJpaService.create(
        ProductEntity.builder().name("Affordable Item").price(BigDecimal.valueOf(30)).build(),
        category.getId());
    productJpaService.create(
        ProductEntity.builder().name("Expensive Item").price(BigDecimal.valueOf(200)).build(),
        category.getId());

    List<ProductSummary> results = productJpaService.getAffordableProducts(BigDecimal.valueOf(50));

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Affordable Item");
    assertThat(results.get(0).getCategoryName()).isEqualTo("Space Gear");
  }

  @Test
  @DisplayName("Should update product")
  void shouldUpdateProduct() {
    ProductEntity saved =
        productJpaService.create(
            ProductEntity.builder()
                .name("Original Name")
                .description("Original desc")
                .price(BigDecimal.valueOf(100))
                .build(),
            category.getId());

    ProductEntity updateData =
        ProductEntity.builder()
            .name("Updated Name")
            .description("Updated desc")
            .price(BigDecimal.valueOf(150))
            .build();

    ProductEntity updated = productJpaService.update(saved.getId(), updateData, category.getId());

    assertThat(updated.getName()).isEqualTo("Updated Name");
    assertThat(updated.getDescription()).isEqualTo("Updated desc");
    assertThat(updated.getPrice()).isEqualByComparingTo("150");
  }

  @Test
  @DisplayName("Should update product with different category")
  void shouldUpdateProductWithDifferentCategory() {
    CategoryEntity newCategory =
        categoryRepository.save(CategoryEntity.builder().name("New Category").build());

    ProductEntity saved =
        productJpaService.create(
            ProductEntity.builder().name("Product").price(BigDecimal.valueOf(100)).build(),
            category.getId());

    ProductEntity updated =
        productJpaService.update(
            saved.getId(),
            ProductEntity.builder().name("Product").price(BigDecimal.valueOf(100)).build(),
            newCategory.getId());

    assertThat(updated.getCategory().getName()).isEqualTo("New Category");
  }

  @Test
  @DisplayName("Should throw exception when updating non-existent product")
  void shouldThrowWhenUpdatingNonExistent() {
    assertThatThrownBy(
            () ->
                productJpaService.update(
                    999L,
                    ProductEntity.builder().name("Test").price(BigDecimal.TEN).build(),
                    category.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Product")
        .hasMessageContaining("999");
  }

  @Test
  @DisplayName("Should delete product")
  void shouldDeleteProduct() {
    ProductEntity saved =
        productJpaService.create(
            ProductEntity.builder().name("To Delete").price(BigDecimal.valueOf(100)).build(),
            category.getId());

    productJpaService.delete(saved.getId());

    assertThat(productJpaService.findById(saved.getId())).isEmpty();
  }

  @Test
  @DisplayName("Should throw NullPointerException when id is null")
  void shouldThrowWhenIdIsNull() {
    assertThatThrownBy(() -> productJpaService.findById(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("must not be null");
  }

  @Test
  @DisplayName("Should throw NullPointerException when categoryId is null")
  void shouldThrowWhenCategoryIdIsNull() {
    assertThatThrownBy(
            () ->
                productJpaService.create(
                    ProductEntity.builder().name("Test").price(BigDecimal.TEN).build(), null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("must not be null");
  }
}
