package com.webjava.lab1.external;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SupplierClient {
  private final RestClient restClient;

  public SupplierClient(@Value("${supplier.base-url:http://localhost:8089}") String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  public List<SupplierProductDTO> getProducts() {
    SupplierProductDTO[] arr =
        restClient
            .get()
            .uri("/api/supplier/products")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(SupplierProductDTO[].class);
    return Arrays.asList(arr != null ? arr : new SupplierProductDTO[0]);
  }
}
