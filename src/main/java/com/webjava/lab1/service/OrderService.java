package com.webjava.lab1.service;

import com.webjava.lab1.domain.Order;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private final Map<UUID, Order> storage = new LinkedHashMap<>();

  public Order create(Order order) {
    UUID id = UUID.randomUUID();
    order.setId(id);
    storage.put(id, order);
    return order;
  }

  public Optional<Order> get(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  public List<Order> list() {
    return new ArrayList<>(storage.values());
  }
}
