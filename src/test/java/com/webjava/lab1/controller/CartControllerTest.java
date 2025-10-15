package com.webjava.lab1.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

  @Autowired private MockMvc mvc;

  @Test
  void deleteIsIdempotentForNonExistingCart() throws Exception {
    String id = UUID.randomUUID().toString();
    mvc.perform(delete("/api/carts/{id}", id)).andExpect(status().isNoContent());
  }
}

