package com.webjava.lab1.service;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(UUID id) {
    super("Product not found: " + id);
  }
}
