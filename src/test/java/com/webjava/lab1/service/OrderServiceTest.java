package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.domain.Order;
import com.webjava.lab1.domain.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

  private OrderService service;

  @BeforeEach
  void setUp() {
    service = new OrderService();
  }

  @Test
  void createAssignsIdentifierAndStoresOrder() {
    Order order = sampleOrder();

    Order created = service.create(order);

    assertThat(created.getId()).isNotNull();
  }

  @Test
  void getReturnsEmptyWhenOrderMissing() {
    assertThat(service.get(UUID.randomUUID())).isEmpty();
  }

  @Test
  void listReturnsCopyOfData() {
    service.create(sampleOrder());

    List<Order> snapshot = service.list();
    assertThat(snapshot).hasSize(1);

    snapshot.clear();

    assertThat(service.list()).hasSize(1);
  }

  private Order sampleOrder() {
    OrderItem item =
        OrderItem.builder()
            .productId(1L)
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("9.99"))
            .build();
    return Order.builder().items(List.of(item)).total(new BigDecimal("19.98")).build();
  }
}
