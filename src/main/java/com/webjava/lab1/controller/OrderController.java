package com.webjava.lab1.controller;

import com.webjava.lab1.domain.Order;
import com.webjava.lab1.dto.OrderDTO;
import com.webjava.lab1.mapper.OrderMapper;
import com.webjava.lab1.service.OrderService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix:/api/v1.2}/orders")
public class OrderController {

  private final OrderService service;
  private final OrderMapper mapper;

  public OrderController(OrderService service, OrderMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @PostMapping
  public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO dto) {
    Order created = service.create(mapper.toEntity(dto));
    return ResponseEntity.status(201).body(mapper.toDto(created));
  }

  @GetMapping
  public List<OrderDTO> list() {
    return mapper.toDtoList(service.list());
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderDTO> get(@PathVariable UUID id) {
    return service
      .get(id)
      .map(o -> ResponseEntity.ok(mapper.toDto(o)))
      .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
