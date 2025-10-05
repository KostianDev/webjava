package com.webjava.lab1.controller;

import com.webjava.lab1.domain.Order;
import com.webjava.lab1.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        Order created = service.create(order);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public List<Order> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable UUID id) {
        return service.get(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
