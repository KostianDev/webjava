package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.domain.Product;
import com.webjava.lab1.entity.CategoryEntity;
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
    Product product =
        Product.builder()
            .name("Space Helmet")
            .description("Protective helmet")
            .price(BigDecimal.valueOf(299.99))
            .categoryId(category.getId())
            .build();

    Product saved = productJpaService.create(product);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("Space Helmet");
    assertThat(saved.getCategoryName()).isEqualTo("Space Gear");
  }

  @Test
  @DisplayName("Should throw when creating product with non-existent category")
  void shouldThrowWhenCategoryNotFound() {
    Product product =
        Product.builder()
            .name("Test Product")
            .price(BigDecimal.valueOf(100))
            .categoryId(999L)
            .build();

    assertThatThrownBy(() -> productJpaService.create(product))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Category")
        .hasMessageContaining("999");
  }

  @Test
  @DisplayName("Should find all products")
  void shouldFindAllProducts() {
    productJpaService.create(
        Product.builder()
            .name("Product 1")
            .price(BigDecimal.valueOf(100))
            .categoryId(category.getId())
            .build());
    productJpaService.create(
        Product.builder()
            .name("Product 2")
            .price(BigDecimal.valueOf(200))
            .categoryId(category.getId())
            .build());

    assertThat(productJpaService.findAll()).hasSize(2);
  }

  @Test
  @DisplayName("Should find product by id")
  void shouldFindProductById() {
    Product saved =
        productJpaService.create(
            Product.builder()
                .name("Unique Product")
                .price(BigDecimal.valueOf(150))
                .categoryId(category.getId())
                .build());

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
        Product.builder()
            .name("In Category")
            .price(BigDecimal.valueOf(100))
            .categoryId(category.getId())
            .build());
    productJpaService.create(
        Product.builder()
            .name("Other")
            .price(BigDecimal.valueOf(100))
            .categoryId(otherCategory.getId())
            .build());

    List<Product> products = productJpaService.findByCategory(category.getId());

    assertThat(products).hasSize(1);
    assertThat(products.get(0).getName()).isEqualTo("In Category");
  }

  @Test
  @DisplayName("Should search products by name")
  void shouldSearchProductsByName() {
    productJpaService.create(
        Product.builder()
            .name("Space Laser")
            .price(BigDecimal.valueOf(500))
            .categoryId(category.getId())
            .build());
    productJpaService.create(
        Product.builder()
            .name("Space Suit")
            .price(BigDecimal.valueOf(300))
            .categoryId(category.getId())
            .build());
    productJpaService.create(
        Product.builder()
            .name("Helmet")
            .price(BigDecimal.valueOf(100))
            .categoryId(category.getId())
            .build());

    List<Product> results = productJpaService.searchByName("space");

    assertThat(results).hasSize(2);
    assertThat(results)
        .extracting(Product::getName)
        .containsExactlyInAnyOrder("Space Laser", "Space Suit");
  }

  @Test
  @DisplayName("Should find products by price range")
  void shouldFindProductsByPriceRange() {
    productJpaService.create(
        Product.builder()
            .name("Cheap")
            .price(BigDecimal.valueOf(50))
            .categoryId(category.getId())
            .build());
    productJpaService.create(
        Product.builder()
            .name("Medium")
            .price(BigDecimal.valueOf(150))
            .categoryId(category.getId())
            .build());
    productJpaService.create(
        Product.builder()
            .name("Expensive")
            .price(BigDecimal.valueOf(500))
            .categoryId(category.getId())
            .build());

    List<Product> results =
        productJpaService.findByPriceRange(BigDecimal.valueOf(100), BigDecimal.valueOf(200));

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Medium");
  }

  @Test
  @DisplayName("Should find affordable products using projection")
  void shouldFindAffordableProducts() {
    productJpaService.create(
        Product.builder()
            .name("Affordable Item")
            .price(BigDecimal.valueOf(30))
            .categoryId(category.getId())
            .build());
    productJpaService.create(
        Product.builder()
            .name("Expensive Item")
            .price(BigDecimal.valueOf(200))
            .categoryId(category.getId())
            .build());

    List<ProductSummary> results = productJpaService.getAffordableProducts(BigDecimal.valueOf(50));

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Affordable Item");
    assertThat(results.get(0).getCategoryName()).isEqualTo("Space Gear");
  }

  @Test
  @DisplayName("Should update product")
  void shouldUpdateProduct() {
    Product saved =
        productJpaService.create(
            Product.builder()
                .name("Original Name")
                .description("Original desc")
                .price(BigDecimal.valueOf(100))
                .categoryId(category.getId())
                .build());

    Product updateData =
        Product.builder()
            .name("Updated Name")
            .description("Updated desc")
            .price(BigDecimal.valueOf(150))
            .categoryId(category.getId())
            .build();

    Product updated = productJpaService.update(saved.getId(), updateData);

    assertThat(updated.getName()).isEqualTo("Updated Name");
    assertThat(updated.getDescription()).isEqualTo("Updated desc");
    assertThat(updated.getPrice()).isEqualByComparingTo("150");
  }

  @Test
  @DisplayName("Should update product with different category")
  void shouldUpdateProductWithDifferentCategory() {
    CategoryEntity newCategory =
        categoryRepository.save(CategoryEntity.builder().name("New Category").build());

    Product saved =
        productJpaService.create(
            Product.builder()
                .name("Product")
                .price(BigDecimal.valueOf(100))
                .categoryId(category.getId())
                .build());

    Product updated =
        productJpaService.update(
            saved.getId(),
            Product.builder()
                .name("Product")
                .price(BigDecimal.valueOf(100))
                .categoryId(newCategory.getId())
                .build());

    assertThat(updated.getCategoryName()).isEqualTo("New Category");
  }

  @Test
  @DisplayName("Should throw exception when updating non-existent product")
  void shouldThrowWhenUpdatingNonExistent() {
    assertThatThrownBy(
            () ->
                productJpaService.update(
                    999L,
                    Product.builder()
                        .name("Test")
                        .price(BigDecimal.TEN)
                        .categoryId(category.getId())
                        .build()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Product")
        .hasMessageContaining("999");
  }

  @Test
  @DisplayName("Should delete product")
  void shouldDeleteProduct() {
    Product saved =
        productJpaService.create(
            Product.builder()
                .name("To Delete")
                .price(BigDecimal.valueOf(100))
                .categoryId(category.getId())
                .build());

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
                    Product.builder().name("Test").price(BigDecimal.TEN).categoryId(null).build()))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("must not be null");
  }
}
