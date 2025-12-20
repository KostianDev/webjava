package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.domain.Cart;
import com.webjava.lab1.domain.CartItem;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = CartService.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CartServiceTest {

  @Autowired
  private CartService service;

  @Test
  void createAssignsIdAndStoresCart() {
    Cart cart = sampleCart();

    Cart created = service.create(cart);

    assertThat(created.getId()).isNotNull();
    assertThat(service.get(created.getId())).contains(created);
  }

  @Test
  void getReturnsEmptyForMissingCart() {
    assertThat(service.get(UUID.randomUUID())).isEmpty();
  }

  @Test
  void deleteRemovesCartWhenPresent() {
    Cart created = service.create(sampleCart());

    service.delete(created.getId());

    assertThat(service.get(created.getId())).isEmpty();
  }

  @Test
  void deleteIgnoresMissingCart() {
    Cart created = service.create(sampleCart());

    service.delete(UUID.randomUUID());

    assertThat(service.get(created.getId())).contains(created);
  }

  private Cart sampleCart() {
    CartItem item = new CartItem(UUID.randomUUID(), 1);
    return new Cart(null, List.of(item));
  }
}
