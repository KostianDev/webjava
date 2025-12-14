package com.webjava.lab1.controller;

import static com.webjava.lab1.web.TraceIdFilter.TRACE_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import({TraceIdFilter.class, OrderControllerTest.TestConfig.class})
class OrderControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private OrderService orderService;

  @AfterEach
  void resetMocks() {
    Mockito.reset(orderService);
  }

  @Test
  void createOrderReturnsCreatedDto() throws Exception {
    UUID productId = UUID.randomUUID();
    OrderDTO requestDto =
        new OrderDTO(
            null,
            List.of(new OrderItemDTO(productId, 1, new BigDecimal("19.99"))),
            new BigDecimal("19.99"));
    Order created =
        Order.builder()
            .id(1L)
            .items(
                List.of(
                    OrderItem.builder()
                        .productId(1L)
                        .productName("Test Product")
                        .quantity(1)
                        .price(new BigDecimal("19.99"))
                        .build()))
            .total(new BigDecimal("19.99"))
            .build();

    when(orderService.create(any(Order.class))).thenReturn(created);

    String payload = Objects.requireNonNull(objectMapper.writeValueAsString(requestDto));

    mockMvc
        .perform(
            post("/api/v1.2/orders").contentType(MediaType.APPLICATION_JSON_VALUE).content(payload))
        .andExpect(status().isCreated())
        .andExpect(header().exists(TRACE_ID_HEADER))
        .andExpect(jsonPath("$.items[0].quantity").value(1));

    ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
    verify(orderService).create(captor.capture());
    Order captured = captor.getValue();
    assertThat(captured.getItems()).hasSize(1);
    assertThat(captured.getItems().getFirst().getQuantity()).isEqualTo(1);
  }

  @Test
  void listOrdersReturnsMappedDtos() throws Exception {
    Order order =
        Order.builder()
            .id(1L)
            .items(
                List.of(
                    OrderItem.builder()
                        .productId(1L)
                        .productName("Test Product")
                        .quantity(2)
                        .price(new BigDecimal("10.00"))
                        .build()))
            .total(new BigDecimal("20.00"))
            .build();

    when(orderService.list()).thenReturn(List.of(order));

    mockMvc
        .perform(get("/api/v1.2/orders"))
        .andExpect(status().isOk())
        .andExpect(header().exists(TRACE_ID_HEADER))
        .andExpect(jsonPath("$[0].items[0].quantity").value(2));

    verify(orderService).list();
  }

  @Test
  void getOrderReturns404WhenMissing() throws Exception {
    UUID id = UUID.randomUUID();
    when(orderService.get(id)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/v1.2/orders/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(header().exists(TRACE_ID_HEADER));

    verify(orderService).get(id);
  }

  @Test
  void getOrderReturnsDtoWhenFound() throws Exception {
    UUID id = UUID.randomUUID();
    Order order =
        Order.builder()
            .id(1L)
            .items(
                List.of(
                    OrderItem.builder()
                        .productId(1L)
                        .productName("Test Product")
                        .quantity(5)
                        .price(new BigDecimal("2.00"))
                        .build()))
            .total(new BigDecimal("10.00"))
            .build();
    when(orderService.get(id)).thenReturn(Optional.of(order));

    mockMvc
        .perform(get("/api/v1.2/orders/{id}", id))
        .andExpect(status().isOk())
        .andExpect(header().exists(TRACE_ID_HEADER))
        .andExpect(jsonPath("$.items[0].quantity").value(5))
        .andExpect(jsonPath("$.total").value(10.00));

    verify(orderService).get(id);
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
