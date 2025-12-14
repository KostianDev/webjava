package com.webjava.lab1.controller;

import com.webjava.lab1.domain.Product;
import com.webjava.lab1.dto.ProductDTO;
import com.webjava.lab1.mapper.ProductMapper;
import com.webjava.lab1.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix:/api/v1.2}/products")
public class ProductController {

  private final ProductService service;
  private final ProductMapper mapper;

  public ProductController(ProductService service, ProductMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @PostMapping
  public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO dto) {
    Product created = service.create(mapper.toEntity(dto));
    return new ResponseEntity<>(mapper.toDto(created), HttpStatus.CREATED);
  }

  @GetMapping
  public List<ProductDTO> list() {
    return service.list().stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductDTO> get(@PathVariable UUID id) {
    return service
        .get(id)
        .map(p -> ResponseEntity.ok(mapper.toDto(p)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductDTO> update(
      @PathVariable UUID id, @Valid @RequestBody ProductDTO dto) {
    Product updated = service.update(id, mapper.toEntity(dto));
    return ResponseEntity.ok(mapper.toDto(updated));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
