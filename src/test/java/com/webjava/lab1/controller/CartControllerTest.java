package com.webjava.lab1.controller;

import static com.webjava.lab1.web.TraceIdFilter.TRACE_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webjava.lab1.domain.Cart;
import com.webjava.lab1.domain.CartItem;
import com.webjava.lab1.dto.CartDTO;
import com.webjava.lab1.dto.CartItemDTO;
import com.webjava.lab1.mapper.CartMapper;
import com.webjava.lab1.mapper.CartMapperImpl;
import com.webjava.lab1.service.CartService;
import com.webjava.lab1.web.TraceIdFilter;
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

@WebMvcTest(CartController.class)
@Import({ TraceIdFilter.class, CartControllerTest.TestConfig.class })
class CartControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CartService cartService;

  @AfterEach
  void resetMocks() {
    Mockito.reset(cartService);
  }

  @Test
  void createCartDelegatesToServiceAndReturnsPayload() throws Exception {
    UUID productId = UUID.randomUUID();
    UUID cartId = UUID.randomUUID();

    CartDTO requestDto = new CartDTO(
      null,
      List.of(new CartItemDTO(productId, 3))
    );
    Cart created = new Cart(cartId, List.of(new CartItem(productId, 3)));

    when(cartService.create(any(Cart.class))).thenReturn(created);

    String payload = Objects.requireNonNull(
      objectMapper.writeValueAsString(requestDto)
    );

    mockMvc
      .perform(
        post("/api/v2/carts")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(payload)
      )
      .andExpect(status().isCreated())
      .andExpect(header().exists(TRACE_ID_HEADER))
      .andExpect(jsonPath("$.id").value(cartId.toString()))
      .andExpect(jsonPath("$.items[0].productId").value(productId.toString()))
      .andExpect(jsonPath("$.items[0].quantity").value(3));

    ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
    verify(cartService).create(captor.capture());
    Cart captured = captor.getValue();
    assertThat(captured.getItems()).hasSize(1);
    assertThat(captured.getItems().getFirst().getProductId()).isEqualTo(
      productId
    );
    assertThat(captured.getItems().getFirst().getQuantity()).isEqualTo(3);
  }

  @Test
  void getCartReturns404WhenMissing() throws Exception {
    UUID id = UUID.randomUUID();
    when(cartService.get(id)).thenReturn(Optional.empty());

    mockMvc
      .perform(get("/api/v2/carts/{id}", id))
      .andExpect(status().isNotFound())
      .andExpect(header().exists(TRACE_ID_HEADER));

    verify(cartService).get(id);
  }

  @Test
  void getCartReturnsMappedDtoWhenFound() throws Exception {
    UUID id = UUID.randomUUID();
    UUID productId = UUID.randomUUID();
    Cart cart = new Cart(id, List.of(new CartItem(productId, 2)));
    when(cartService.get(id)).thenReturn(Optional.of(cart));

    mockMvc
      .perform(get("/api/v2/carts/{id}", id))
      .andExpect(status().isOk())
      .andExpect(header().exists(TRACE_ID_HEADER))
      .andExpect(jsonPath("$.items[0].quantity").value(2));

    verify(cartService).get(id);
  }

  @Test
  void deleteCartReturnsNoContentAndInvokesService() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc
      .perform(delete("/api/v2/carts/{id}", id))
      .andExpect(status().isNoContent())
      .andExpect(header().exists(TRACE_ID_HEADER));

    verify(cartService).delete(id);
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    CartMapper cartMapper() {
      return new CartMapperImpl();
    }

    @Bean
    CartService cartService() {
      return Mockito.mock(CartService.class);
    }
  }
}
