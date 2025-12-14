package com.webjava.lab1.controller;

import com.webjava.lab1.domain.Cart;
import com.webjava.lab1.dto.CartDTO;
import com.webjava.lab1.mapper.CartMapper;
import com.webjava.lab1.service.CartService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix:/api/v4}/carts")
public class CartController {

  private final CartService service;
  private final CartMapper mapper;

  public CartController(CartService service, CartMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @PostMapping
  public ResponseEntity<CartDTO> create(@RequestBody CartDTO cartDto) {
    Cart created = service.create(mapper.toEntity(cartDto));
    return ResponseEntity.status(201).body(mapper.toDto(created));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CartDTO> get(@PathVariable UUID id) {
    return service
        .get(id)
        .map(c -> ResponseEntity.ok(mapper.toDto(c)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
