package com.webjava.lab1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webjava.lab1.domain.Order;
import com.webjava.lab1.domain.OrderItem;
import com.webjava.lab1.dto.OrderDTO;
import com.webjava.lab1.dto.OrderItemDTO;
import com.webjava.lab1.mapper.OrderMapper;
import com.webjava.lab1.mapper.OrderMapperImpl;
import com.webjava.lab1.service.OrderService;
import com.webjava.lab1.web.TraceIdFilter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import({TraceIdFilter.class, OrderControllerTest.TestConfig.class})
class OrderControllerTest {

  @org.springframework.beans.factory.annotation.Autowired private MockMvc mockMvc;
  @org.springframework.beans.factory.annotation.Autowired private ObjectMapper objectMapper;
  @org.springframework.beans.factory.annotation.Autowired private OrderService orderService;

  @AfterEach
  void resetMocks() {
    Mockito.reset(orderService);
  }

  @Test
  void createOrderReturnsCreatedDto() throws Exception {
    UUID productId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();
    OrderDTO request =
        new OrderDTO(
            null,
            List.of(new OrderItemDTO(productId, 1, new BigDecimal("19.99"))),
            new BigDecimal("19.99"));
    Order created =
        new Order(
            orderId,
            List.of(new OrderItem(productId, 1, new BigDecimal("19.99"))),
            new BigDecimal("19.99"));

    when(orderService.create(any(Order.class))).thenReturn(created);

    mockMvc
        .perform(
            post("/api/v1.1/orders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
        .andExpect(status().isCreated())
    .andExpect(header().exists("X-Trace-Id"))
        .andExpect(jsonPath("$.id").value(orderId.toString()))
        .andExpect(jsonPath("$.items[0].productId").value(productId.toString()));

    verify(orderService).create(any(Order.class));
  }

  @Test
  void listOrdersReturnsMappedDtos() throws Exception {
    UUID orderId = UUID.randomUUID();
    UUID productId = UUID.randomUUID();
    Order order =
        new Order(
            orderId,
            List.of(new OrderItem(productId, 2, new BigDecimal("10.00"))),
            new BigDecimal("20.00"));

    when(orderService.list()).thenReturn(List.of(order));

    mockMvc
        .perform(get("/api/v1.1/orders"))
    .andExpect(status().isOk())
    .andExpect(header().exists("X-Trace-Id"))
        .andExpect(jsonPath("$[0].id").value(orderId.toString()))
        .andExpect(jsonPath("$[0].items[0].quantity").value(2));
  }

  @Test
  void getOrderReturns404WhenMissing() throws Exception {
    UUID id = UUID.randomUUID();
    when(orderService.get(id)).thenReturn(Optional.empty());

  mockMvc
    .perform(get("/api/v1.1/orders/{id}", id))
    .andExpect(status().isNotFound())
    .andExpect(header().exists("X-Trace-Id"));
  }

  @Test
  void getOrderReturnsDtoWhenFound() throws Exception {
    UUID id = UUID.randomUUID();
    UUID productId = UUID.randomUUID();
    Order order =
        new Order(
            id,
            List.of(new OrderItem(productId, 5, new BigDecimal("2.00"))),
            new BigDecimal("10.00"));
    when(orderService.get(id)).thenReturn(Optional.of(order));

    mockMvc
        .perform(get("/api/v1.1/orders/{id}", id))
    .andExpect(status().isOk())
    .andExpect(header().exists("X-Trace-Id"))
        .andExpect(jsonPath("$.items[0].quantity").value(5))
        .andExpect(jsonPath("$.total").value(10.00));
  }

  @TestConfiguration
  static class TestConfig {
    @Bean
    OrderMapper orderMapper() {
      return new OrderMapperImpl();
    }

    @Bean
    OrderService orderService() {
      return Mockito.mock(OrderService.class);
    }
  }
}
