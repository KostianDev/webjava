package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.domain.Order;
import com.webjava.lab1.domain.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = OrderService.class)
class OrderServiceTest {

  @Autowired
  private OrderService service;

  @Test
  void createAssignsIdentifierAndStoresOrder() {
    Order order = sampleOrder();

    Order created = service.create(order);

    assertThat(created.getId()).isNotNull();
    assertThat(service.get(created.getId())).contains(created);
  }

  @Test
  void getReturnsEmptyWhenOrderMissing() {
    assertThat(service.get(UUID.randomUUID())).isEmpty();
  }

  @Test
  void listReturnsCopyOfData() {
    Order created = service.create(sampleOrder());

    List<Order> snapshot = service.list();
    assertThat(snapshot).contains(created);

    snapshot.clear();

    assertThat(service.list()).contains(created);
  }

  private Order sampleOrder() {
    OrderItem item = new OrderItem(
      UUID.randomUUID(),
      2,
      new BigDecimal("9.99")
    );
    return new Order(null, List.of(item), new BigDecimal("19.98"));
  }
}
