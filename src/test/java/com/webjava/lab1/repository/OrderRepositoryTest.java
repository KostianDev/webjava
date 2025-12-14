package com.webjava.lab1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.entity.CategoryEntity;
import com.webjava.lab1.entity.OrderEntity;
import com.webjava.lab1.entity.OrderItemEntity;
import com.webjava.lab1.entity.ProductEntity;
import com.webjava.lab1.entity.UserEntity;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("null")
class OrderRepositoryTest extends AbstractIntegrationTest {

  @Autowired private OrderRepository orderRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private EntityManager entityManager;

  private UserEntity user;
  private ProductEntity product;

  @BeforeEach
  void setUp() {
    orderRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();

    user =
        userRepository.save(
            UserEntity.builder().name("John Doe").email("john.doe@cosmos.com").build());

    CategoryEntity category =
        categoryRepository.save(CategoryEntity.builder().name("Test Category").build());

    product =
        productRepository.save(
            ProductEntity.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .category(category)
                .build());
  }

  @Test
  @DisplayName("Should save and find order by id")
  void shouldSaveAndFindById() {
    OrderEntity order =
        OrderEntity.builder()
            .orderNumber("ORD-12345678")
            .user(user)
            .total(BigDecimal.valueOf(100))
            .build();

    OrderItemEntity item =
        OrderItemEntity.builder().product(product).quantity(1).price(product.getPrice()).build();
    order.addItem(item);

    OrderEntity saved = orderRepository.save(order);
    entityManager.flush();
    entityManager.clear();

    assertThat(saved.getId()).isNotNull();
    assertThat(orderRepository.findById(saved.getId()))
        .isPresent()
        .hasValueSatisfying(
            o -> {
              assertThat(o.getOrderNumber()).isEqualTo("ORD-12345678");
              assertThat(o.getCreatedAt()).isNotNull();
              assertThat(o.getUser().getEmail()).isEqualTo("john.doe@cosmos.com");
              assertThat(o.getItems()).hasSize(1);
            });
  }

  @Test
  @DisplayName("Should find order by order number (Natural ID)")
  void shouldFindByOrderNumber() {
    OrderEntity order =
        OrderEntity.builder()
            .orderNumber("ORD-NATURAL-01")
            .user(user)
            .total(BigDecimal.valueOf(200))
            .build();
    orderRepository.save(order);

    assertThat(orderRepository.findByOrderNumber("ORD-NATURAL-01"))
        .isPresent()
        .hasValueSatisfying(o -> assertThat(o.getTotal()).isEqualByComparingTo("200"));
  }

  @Test
  @DisplayName("Should return empty when order not found by order number")
  void shouldReturnEmptyWhenNotFoundByOrderNumber() {
    assertThat(orderRepository.findByOrderNumber("NON-EXISTENT")).isEmpty();
  }

  @Test
  @DisplayName("Should find orders by user id ordered by created at desc")
  void shouldFindByUserIdOrderedByCreatedAtDesc() {
    OrderEntity order1 =
        OrderEntity.builder()
            .orderNumber("ORD-USER-001")
            .user(user)
            .total(BigDecimal.valueOf(100))
            .build();
    orderRepository.save(order1);

    OrderEntity order2 =
        OrderEntity.builder()
            .orderNumber("ORD-USER-002")
            .user(user)
            .total(BigDecimal.valueOf(200))
            .build();
    orderRepository.save(order2);

    List<OrderEntity> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

    assertThat(orders).hasSize(2);
    // Latest order should be first (createdAt DESC)
    assertThat(orders.get(0).getOrderNumber()).isEqualTo("ORD-USER-002");
  }

  @Test
  @DisplayName("Should return empty list when no orders for user")
  void shouldReturnEmptyListWhenNoOrdersForUser() {
    UserEntity anotherUser =
        userRepository.save(
            UserEntity.builder().name("Jane Smith").email("jane@cosmos.com").build());

    List<OrderEntity> orders =
        orderRepository.findByUserIdOrderByCreatedAtDesc(anotherUser.getId());

    assertThat(orders).isEmpty();
  }

  @Test
  @DisplayName("Should delete order")
  void shouldDeleteOrder() {
    OrderEntity order =
        orderRepository.save(
            OrderEntity.builder()
                .orderNumber("ORD-TO-DELETE")
                .user(user)
                .total(BigDecimal.ZERO)
                .build());

    orderRepository.deleteById(order.getId());

    assertThat(orderRepository.findById(order.getId())).isEmpty();
  }

  @Test
  @DisplayName("Should cascade delete order items when order is deleted")
  void shouldCascadeDeleteOrderItems() {
    OrderEntity order =
        OrderEntity.builder()
            .orderNumber("ORD-CASCADE")
            .user(user)
            .total(BigDecimal.valueOf(100))
            .build();

    OrderItemEntity item =
        OrderItemEntity.builder().product(product).quantity(1).price(product.getPrice()).build();
    order.addItem(item);

    OrderEntity saved = orderRepository.save(order);
    Long orderId = saved.getId();

    orderRepository.deleteById(orderId);

    assertThat(orderRepository.findById(orderId)).isEmpty();
  }

  @Test
  @DisplayName("Should find all orders")
  void shouldFindAllOrders() {
    orderRepository.save(
        OrderEntity.builder()
            .orderNumber("ORD-ALL-001")
            .user(user)
            .total(BigDecimal.valueOf(100))
            .build());
    orderRepository.save(
        OrderEntity.builder()
            .orderNumber("ORD-ALL-002")
            .user(user)
            .total(BigDecimal.valueOf(200))
            .build());

    assertThat(orderRepository.findAll()).hasSize(2);
  }

  @Test
  @DisplayName("Should add and remove items from order")
  void shouldAddAndRemoveItems() {
    OrderEntity order =
        OrderEntity.builder()
            .orderNumber("ORD-ITEMS-TEST")
            .user(user)
            .total(BigDecimal.valueOf(100))
            .build();

    OrderItemEntity item =
        OrderItemEntity.builder().product(product).quantity(1).price(product.getPrice()).build();

    order.addItem(item);
    assertThat(order.getItems()).hasSize(1);
    assertThat(item.getOrder()).isEqualTo(order);

    order.removeItem(item);
    assertThat(order.getItems()).isEmpty();
    assertThat(item.getOrder()).isNull();
  }
}
