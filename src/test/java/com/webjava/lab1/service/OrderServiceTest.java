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
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = OrderService.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
    assertThat(snapshot).containsExactly(created);

    snapshot.clear();

    assertThat(service.list()).containsExactly(created);
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
