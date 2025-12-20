package com.webjava.lab1.controller;

import com.webjava.lab1.external.SupplierClient;
import com.webjava.lab1.external.SupplierProductDTO;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix:/api/v1.2}/supplier")
public class SupplierController {

  private final SupplierClient client;

  public SupplierController(SupplierClient client) {
    this.client = client;
  }

  @GetMapping("/products")
  public List<SupplierProductDTO> list() {
    return client.getProducts();
  }
}
