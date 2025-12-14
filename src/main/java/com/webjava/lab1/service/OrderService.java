package com.webjava.lab1.service;

import com.webjava.lab1.domain.Order;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private final Map<UUID, Order> storage = new LinkedHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1L);

  public Order create(Order order) {
    Long id = idGenerator.getAndIncrement();
    UUID uuid = UUID.randomUUID();
    order.setId(id);
    storage.put(uuid, order);
    return order;
  }

  public Optional<Order> get(UUID uuid) {
    return Optional.ofNullable(storage.get(uuid));
  }

  public List<Order> list() {
    return new ArrayList<>(storage.values());
  }
}
