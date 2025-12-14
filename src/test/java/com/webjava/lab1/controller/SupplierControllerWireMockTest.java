package com.webjava.lab1.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.webjava.lab1.AbstractIntegrationTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class SupplierControllerWireMockTest extends AbstractIntegrationTest {

  @RegisterExtension
  static WireMockExtension wireMock =
      WireMockExtension.newInstance()
          .options(
              com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig()
                  .dynamicPort())
          .build();

  @DynamicPropertySource
  static void overrideBaseUrl(DynamicPropertyRegistry registry) {
    registry.add("supplier.base-url", wireMock::baseUrl);
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void listProductsReturnsExternalPayload() throws Exception {
    UUID productId = UUID.randomUUID();
    Map<String, Object> supplierProduct =
        Map.of(
            "id", productId.toString(), "name", "Quantum Grapes", "price", new BigDecimal("42.00"));

    wireMock.stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/api/supplier/products"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(List.of(supplierProduct)))));

    mockMvc
        .perform(get("/api/v4/supplier/products"))
        .andExpect(status().isOk())
        .andExpect(header().exists("X-Trace-Id"))
        .andExpect(jsonPath("$[0].id").value(productId.toString()))
        .andExpect(jsonPath("$[0].name").value("Quantum Grapes"))
        .andExpect(jsonPath("$[0].price").value(42.0));

    wireMock.verify(
        getRequestedFor(urlEqualTo("/api/supplier/products"))
            .withHeader("Accept", equalTo("application/json")));
  }

  @Test
  void listProductsGracefullyHandlesEmptySupplierResponse() throws Exception {
    wireMock.stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/api/supplier/products"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("[]")));

    mockMvc
        .perform(get("/api/v4/supplier/products"))
        .andExpect(status().isOk())
        .andExpect(header().exists("X-Trace-Id"))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

    wireMock.verify(getRequestedFor(urlEqualTo("/api/supplier/products")));
  }
}
