package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.domain.Order;
import com.webjava.lab1.entity.CategoryEntity;
import com.webjava.lab1.entity.OrderEntity;
import com.webjava.lab1.entity.ProductEntity;
import com.webjava.lab1.entity.UserEntity;
import com.webjava.lab1.repository.CategoryRepository;
import com.webjava.lab1.repository.OrderRepository;
import com.webjava.lab1.repository.ProductRepository;
import com.webjava.lab1.repository.UserRepository;
import com.webjava.lab1.service.OrderJpaService.OrderItemRequest;
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
class OrderJpaServiceIntegrationTest extends AbstractIntegrationTest {

  @Autowired private OrderJpaService orderJpaService;
  @Autowired private OrderRepository orderRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private EntityManager entityManager;

  private UserEntity user;
  private ProductEntity product1;
  private ProductEntity product2;

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

    product1 =
        productRepository.save(
            ProductEntity.builder()
                .name("Product 1")
                .price(BigDecimal.valueOf(100))
                .category(category)
                .build());

    product2 =
        productRepository.save(
            ProductEntity.builder()
                .name("Product 2")
                .price(BigDecimal.valueOf(50))
                .category(category)
                .build());
  }

  @Test
  @DisplayName("Should create order with items")
  void shouldCreateOrderWithItems() {
    List<OrderItemRequest> items =
        List.of(
            new OrderItemRequest(product1.getId(), 2), new OrderItemRequest(product2.getId(), 3));

    Order order = orderJpaService.create(user.getId(), items);
    entityManager.flush();
    entityManager.clear();

    assertThat(order.getId()).isNotNull();
    assertThat(order.getOrderNumber()).startsWith("ORD-");

    // Verify details by re-fetching from database
    OrderEntity fetched = orderRepository.findById(order.getId()).orElseThrow();
    assertThat(fetched.getUser().getEmail()).isEqualTo("john.doe@cosmos.com");
    assertThat(fetched.getItems()).hasSize(2);
    assertThat(fetched.getTotal()).isEqualByComparingTo("350"); // 100*2 + 50*3
    assertThat(fetched.getCreatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should throw when creating order with non-existent user")
  void shouldThrowWhenUserNotFound() {
    List<OrderItemRequest> items = List.of(new OrderItemRequest(product1.getId(), 1));

    assertThatThrownBy(() -> orderJpaService.create(999L, items))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("User")
        .hasMessageContaining("999");
  }

  @Test
  @DisplayName("Should throw when creating order with non-existent product")
  void shouldThrowWhenProductNotFound() {
    List<OrderItemRequest> items = List.of(new OrderItemRequest(999L, 1));

    assertThatThrownBy(() -> orderJpaService.create(user.getId(), items))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Product")
        .hasMessageContaining("999");
  }

  @Test
  @DisplayName("Should find all orders")
  void shouldFindAllOrders() {
    orderJpaService.create(user.getId(), List.of(new OrderItemRequest(product1.getId(), 1)));
    orderJpaService.create(user.getId(), List.of(new OrderItemRequest(product2.getId(), 1)));

    assertThat(orderJpaService.findAll()).hasSize(2);
  }

  @Test
  @DisplayName("Should find order by id")
  void shouldFindOrderById() {
    Order created =
        orderJpaService.create(user.getId(), List.of(new OrderItemRequest(product1.getId(), 1)));

    assertThat(orderJpaService.findById(created.getId()))
        .isPresent()
        .hasValueSatisfying(
            o -> {
              assertThat(o.getOrderNumber()).isEqualTo(created.getOrderNumber());
              assertThat(o.getItems()).hasSize(1);
            });
  }

  @Test
  @DisplayName("Should return empty when order not found by id")
  void shouldReturnEmptyWhenNotFoundById() {
    assertThat(orderJpaService.findById(999L)).isEmpty();
  }

  @Test
  @DisplayName("Should find order by order number (Natural ID)")
  void shouldFindOrderByOrderNumber() {
    Order created =
        orderJpaService.create(user.getId(), List.of(new OrderItemRequest(product1.getId(), 1)));

    assertThat(orderJpaService.findByOrderNumber(created.getOrderNumber()))
        .isPresent()
        .hasValueSatisfying(o -> assertThat(o.getId()).isEqualTo(created.getId()));
  }

  @Test
  @DisplayName("Should return empty when order not found by order number")
  void shouldReturnEmptyWhenNotFoundByOrderNumber() {
    assertThat(orderJpaService.findByOrderNumber("NON-EXISTENT")).isEmpty();
  }

  @Test
  @DisplayName("Should find orders by user")
  void shouldFindOrdersByUser() {
    UserEntity anotherUser =
        userRepository.save(
            UserEntity.builder().name("Jane Smith").email("jane@cosmos.com").build());

    orderJpaService.create(user.getId(), List.of(new OrderItemRequest(product1.getId(), 1)));
    orderJpaService.create(user.getId(), List.of(new OrderItemRequest(product2.getId(), 1)));
    orderJpaService.create(anotherUser.getId(), List.of(new OrderItemRequest(product1.getId(), 1)));

    List<Order> userOrders = orderJpaService.findByUser(user.getId());

    assertThat(userOrders).hasSize(2);
    assertThat(userOrders).allMatch(o -> o.getUserId().equals(user.getId()));
  }

  @Test
  @DisplayName("Should return empty list when no orders for user")
  void shouldReturnEmptyListWhenNoOrders() {
    UserEntity newUser =
        userRepository.save(UserEntity.builder().name("New User").email("new@cosmos.com").build());

    assertThat(orderJpaService.findByUser(newUser.getId())).isEmpty();
  }

  @Test
  @DisplayName("Should delete order")
  void shouldDeleteOrder() {
    Order created =
        orderJpaService.create(user.getId(), List.of(new OrderItemRequest(product1.getId(), 1)));

    orderJpaService.delete(created.getId());

    assertThat(orderJpaService.findById(created.getId())).isEmpty();
  }

  @Test
  @DisplayName("Should generate unique order numbers")
  void shouldGenerateUniqueOrderNumbers() {
    Order order1 =
        orderJpaService.create(user.getId(), List.of(new OrderItemRequest(product1.getId(), 1)));
    Order order2 =
        orderJpaService.create(user.getId(), List.of(new OrderItemRequest(product1.getId(), 1)));

    assertThat(order1.getOrderNumber()).isNotEqualTo(order2.getOrderNumber());
  }

  @Test
  @DisplayName("Should throw NullPointerException when userId is null")
  void shouldThrowWhenUserIdIsNull() {
    assertThatThrownBy(
            () -> orderJpaService.create(null, List.of(new OrderItemRequest(product1.getId(), 1))))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("userId");
  }

  @Test
  @DisplayName("Should throw NullPointerException when productId is null")
  void shouldThrowWhenProductIdIsNull() {
    assertThatThrownBy(
            () -> orderJpaService.create(user.getId(), List.of(new OrderItemRequest(null, 1))))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("productId");
  }

  @Test
  @DisplayName("Should correctly calculate total for multiple items")
  void shouldCorrectlyCalculateTotal() {
    List<OrderItemRequest> items =
        List.of(
            new OrderItemRequest(product1.getId(), 5), // 100 * 5 = 500
            new OrderItemRequest(product2.getId(), 10) // 50 * 10 = 500
            );

    Order order = orderJpaService.create(user.getId(), items);

    assertThat(order.getTotal()).isEqualByComparingTo("1000");
  }

  @Test
  @DisplayName("Should store correct price snapshot in order item")
  void shouldStorePriceSnapshotInOrderItem() {
    Order order =
        orderJpaService.create(user.getId(), List.of(new OrderItemRequest(product1.getId(), 1)));

    assertThat(order.getItems().get(0).getPrice()).isEqualByComparingTo(product1.getPrice());
  }
}
