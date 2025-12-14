package com.webjava.lab1.service;

import com.webjava.lab1.domain.Product;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final Map<UUID, Product> storage = new LinkedHashMap<>();
  private final Map<Long, UUID> idMapping = new LinkedHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1L);

  @PostConstruct
  public void init() {
    // sample data
    Long id1 = idGenerator.getAndIncrement();
    UUID uuid1 = UUID.randomUUID();
    Product p1 =
        Product.builder()
            .id(id1)
            .name("Star Yarn")
            .description("Antigravity yarn for space knitting")
            .price(new BigDecimal("9.99"))
            .categoryName("Textiles")
            .build();
    storage.put(uuid1, p1);
    idMapping.put(id1, uuid1);

    Long id2 = idGenerator.getAndIncrement();
    UUID uuid2 = UUID.randomUUID();
    Product p2 =
        Product.builder()
            .id(id2)
            .name("Galaxy Milk")
            .description("Enriched milk from the Andromeda herds")
            .price(new BigDecimal("4.50"))
            .categoryName("Food")
            .build();
    storage.put(uuid2, p2);
    idMapping.put(id2, uuid2);
  }

  public Product create(Product product) {
    Long id = idGenerator.getAndIncrement();
    UUID uuid = UUID.randomUUID();
    product.setId(id);
    storage.put(uuid, product);
    idMapping.put(id, uuid);
    return product;
  }

  public List<Product> list() {
    return new ArrayList<>(storage.values());
  }

  public Optional<Product> get(UUID uuid) {
    return Optional.ofNullable(storage.get(uuid));
  }

  public Product update(UUID uuid, Product product) {
    if (!storage.containsKey(uuid)) throw new ProductNotFoundException(uuid);
    Product existing = storage.get(uuid);
    product.setId(existing.getId());
    storage.put(uuid, product);
    return product;
  }

  public void delete(UUID uuid) {
    // idempotent delete: removing a non-existing id is a no-op
    Product removed = storage.remove(uuid);
    if (removed != null) {
      idMapping.remove(removed.getId());
    }
  }
}
