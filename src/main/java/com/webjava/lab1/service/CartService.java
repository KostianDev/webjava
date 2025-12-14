package com.webjava.lab1.service;

import com.webjava.lab1.domain.Cart;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CartService {

  private final Map<UUID, Cart> storage = new LinkedHashMap<>();

  public Cart create(Cart cart) {
    UUID id = UUID.randomUUID();
    cart.setId(id);
    storage.put(id, cart);
    return cart;
  }

  public Optional<Cart> get(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  public void delete(UUID id) {
    storage.remove(id);
  }
}
