package com.webjava.lab1.controller;

import com.webjava.lab1.domain.Cart;
import com.webjava.lab1.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Cart> create(@RequestBody Cart cart) {
        Cart created = service.create(cart);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> get(@PathVariable UUID id) {
        return service.get(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
