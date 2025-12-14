package com.webjava.lab1.service;

import com.webjava.lab1.domain.Product;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final Map<UUID, Product> storage = new LinkedHashMap<>();

  @PostConstruct
  public void init() {
    // sample data
    Product p1 =
        new Product(
            UUID.randomUUID(),
            "Star Yarn",
            "Antigravity yarn for space knitting",
            new BigDecimal("9.99"),
            "Textiles");
    Product p2 =
        new Product(
            UUID.randomUUID(),
            "Galaxy Milk",
            "Enriched milk from the Andromeda herds",
            new BigDecimal("4.50"),
            "Food");
    storage.put(p1.getId(), p1);
    storage.put(p2.getId(), p2);
  }

  public Product create(Product product) {
    UUID id = UUID.randomUUID();
    product.setId(id);
    storage.put(id, product);
    return product;
  }

  public List<Product> list() {
    return new ArrayList<>(storage.values());
  }

  public Optional<Product> get(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  public Product update(UUID id, Product product) {
    if (!storage.containsKey(id)) throw new ProductNotFoundException(id);
    product.setId(id);
    storage.put(id, product);
    return product;
  }

  public void delete(UUID id) {
    // idempotent delete: removing a non-existing id is a no-op
    storage.remove(id);
  }
}
