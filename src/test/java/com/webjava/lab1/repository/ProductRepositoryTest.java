package com.webjava.lab1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.entity.CategoryEntity;
import com.webjava.lab1.entity.OrderEntity;
import com.webjava.lab1.entity.OrderItemEntity;
import com.webjava.lab1.entity.ProductEntity;
import com.webjava.lab1.entity.UserEntity;
import com.webjava.lab1.projection.ProductSalesReport;
import com.webjava.lab1.projection.ProductSummary;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("null")
class ProductRepositoryTest extends AbstractIntegrationTest {

  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private UserRepository userRepository;

  private CategoryEntity category;

  @BeforeEach
  void setUp() {
    orderRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();

    category =
        categoryRepository.save(
            CategoryEntity.builder().name("Space Tech").description("Space technology").build());
  }

  @Test
  @DisplayName("Should save and find product by id")
  void shouldSaveAndFindById() {
    ProductEntity product =
        ProductEntity.builder()
            .name("Laser Gun")
            .description("Powerful laser")
            .price(BigDecimal.valueOf(999.99))
            .category(category)
            .build();

    ProductEntity saved = productRepository.save(product);

    assertThat(saved.getId()).isNotNull();
    assertThat(productRepository.findById(saved.getId()))
        .isPresent()
        .hasValueSatisfying(
            p -> {
              assertThat(p.getName()).isEqualTo("Laser Gun");
              assertThat(p.getCategory().getName()).isEqualTo("Space Tech");
            });
  }

  @Test
  @DisplayName("Should find products by name containing")
  void shouldFindByNameContaining() {
    productRepository.save(
        ProductEntity.builder()
            .name("Space Helmet")
            .price(BigDecimal.valueOf(100))
            .category(category)
            .build());
    productRepository.save(
        ProductEntity.builder()
            .name("Space Suit")
            .price(BigDecimal.valueOf(500))
            .category(category)
            .build());
    productRepository.save(
        ProductEntity.builder()
            .name("Laser Gun")
            .price(BigDecimal.valueOf(300))
            .category(category)
            .build());

    List<ProductEntity> results = productRepository.findByNameContainingIgnoreCase("space");

    assertThat(results).hasSize(2);
    assertThat(results)
        .extracting(ProductEntity::getName)
        .containsExactlyInAnyOrder("Space Helmet", "Space Suit");
  }

  @Test
  @DisplayName("Should find products by category id")
  void shouldFindByCategoryId() {
    CategoryEntity anotherCategory =
        categoryRepository.save(CategoryEntity.builder().name("Food").build());

    productRepository.save(
        ProductEntity.builder()
            .name("Product1")
            .price(BigDecimal.valueOf(10))
            .category(category)
            .build());
    productRepository.save(
        ProductEntity.builder()
            .name("Product2")
            .price(BigDecimal.valueOf(20))
            .category(anotherCategory)
            .build());

    List<ProductEntity> results = productRepository.findByCategoryId(category.getId());

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Product1");
  }

  @Test
  @DisplayName("Should find product by name and category id")
  void shouldFindByNameAndCategoryId() {
    productRepository.save(
        ProductEntity.builder()
            .name("UniqueProduct")
            .price(BigDecimal.valueOf(50))
            .category(category)
            .build());

    assertThat(productRepository.findByNameAndCategoryId("UniqueProduct", category.getId()))
        .isPresent();
    assertThat(productRepository.findByNameAndCategoryId("NonExistent", category.getId()))
        .isEmpty();
  }

  @Test
  @DisplayName("Should check if product exists by name and category id")
  void shouldCheckExistsByNameAndCategoryId() {
    productRepository.save(
        ProductEntity.builder()
            .name("ExistingProduct")
            .price(BigDecimal.valueOf(75))
            .category(category)
            .build());

    assertThat(productRepository.existsByNameAndCategoryId("ExistingProduct", category.getId()))
        .isTrue();
    assertThat(productRepository.existsByNameAndCategoryId("NonExistent", category.getId()))
        .isFalse();
  }

  @Test
  @DisplayName("Should find products by price range")
  void shouldFindByPriceRange() {
    productRepository.save(
        ProductEntity.builder()
            .name("Cheap")
            .price(BigDecimal.valueOf(10))
            .category(category)
            .build());
    productRepository.save(
        ProductEntity.builder()
            .name("Medium")
            .price(BigDecimal.valueOf(50))
            .category(category)
            .build());
    productRepository.save(
        ProductEntity.builder()
            .name("Expensive")
            .price(BigDecimal.valueOf(100))
            .category(category)
            .build());

    List<ProductEntity> results =
        productRepository.findByPriceRange(BigDecimal.valueOf(20), BigDecimal.valueOf(80));

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Medium");
  }

  @Test
  @DisplayName("Should find top selling products")
  void shouldFindTopSellingProducts() {
    ProductEntity product1 =
        productRepository.save(
            ProductEntity.builder()
                .name("BestSeller")
                .price(BigDecimal.valueOf(100))
                .category(category)
                .build());
    ProductEntity product2 =
        productRepository.save(
            ProductEntity.builder()
                .name("SecondBest")
                .price(BigDecimal.valueOf(50))
                .category(category)
                .build());

    UserEntity user =
        userRepository.save(
            UserEntity.builder().name("Test User").email("test@cosmos.com").build());

    OrderEntity order =
        OrderEntity.builder().orderNumber("ORD-TEST-001").user(user).total(BigDecimal.ZERO).build();

    OrderItemEntity item1 =
        OrderItemEntity.builder().product(product1).quantity(10).price(product1.getPrice()).build();
    OrderItemEntity item2 =
        OrderItemEntity.builder().product(product2).quantity(5).price(product2.getPrice()).build();

    order.addItem(item1);
    order.addItem(item2);
    order.setTotal(BigDecimal.valueOf(1250));
    orderRepository.save(order);

    List<ProductSalesReport> reports = productRepository.findTopSellingProducts();

    assertThat(reports).hasSize(2);
    assertThat(reports.get(0).getProductName()).isEqualTo("BestSeller");
    assertThat(reports.get(0).getTotalQuantitySold()).isEqualTo(10L);
    assertThat(reports.get(1).getProductName()).isEqualTo("SecondBest");
  }

  @Test
  @DisplayName("Should find top selling products by category")
  void shouldFindTopSellingProductsByCategory() {
    CategoryEntity otherCategory =
        categoryRepository.save(CategoryEntity.builder().name("Other").build());

    ProductEntity product1 =
        productRepository.save(
            ProductEntity.builder()
                .name("InCategory")
                .price(BigDecimal.valueOf(100))
                .category(category)
                .build());
    ProductEntity product2 =
        productRepository.save(
            ProductEntity.builder()
                .name("OutOfCategory")
                .price(BigDecimal.valueOf(50))
                .category(otherCategory)
                .build());

    UserEntity user =
        userRepository.save(
            UserEntity.builder().name("Test User").email("test2@cosmos.com").build());

    OrderEntity order =
        OrderEntity.builder().orderNumber("ORD-TEST-002").user(user).total(BigDecimal.ZERO).build();

    order.addItem(
        OrderItemEntity.builder().product(product1).quantity(5).price(product1.getPrice()).build());
    order.addItem(
        OrderItemEntity.builder().product(product2).quantity(3).price(product2.getPrice()).build());
    order.setTotal(BigDecimal.valueOf(650));
    orderRepository.save(order);

    List<ProductSalesReport> reports =
        productRepository.findTopSellingProductsByCategory(category.getId());

    assertThat(reports).hasSize(1);
    assertThat(reports.get(0).getProductName()).isEqualTo("InCategory");
  }

  @Test
  @DisplayName("Should find affordable products")
  void shouldFindAffordableProducts() {
    productRepository.save(
        ProductEntity.builder()
            .name("Affordable")
            .price(BigDecimal.valueOf(25))
            .category(category)
            .build());
    productRepository.save(
        ProductEntity.builder()
            .name("Expensive")
            .price(BigDecimal.valueOf(100))
            .category(category)
            .build());

    List<ProductSummary> results = productRepository.findAffordableProducts(BigDecimal.valueOf(50));

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Affordable");
    assertThat(results.get(0).getCategoryName()).isEqualTo("Space Tech");
  }
}
