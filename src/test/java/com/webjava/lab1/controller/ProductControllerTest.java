package com.webjava.lab1.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webjava.lab1.dto.ProductDTO;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper mapper;

  @Test
  void createProductHappyPath() throws Exception {
    ProductDTO dto = new ProductDTO();
    dto.setName("Star Yarn");
    dto.setDescription("Antigravity yarn for space knitting");
    dto.setPrice(new BigDecimal("9.99"));
    dto.setCategory("Textiles");

    mvc.perform(
            post("/api/v1.1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Star Yarn"));
  }

  @Test
  void createProductValidationError() throws Exception {
    ProductDTO dto = new ProductDTO();
    dto.setName(""); // invalid: NotBlank and cosmic check
    dto.setDescription("No name");
    dto.setPrice(new BigDecimal("0")); // invalid: DecimalMin 0.01
    dto.setCategory("");

    mvc.perform(
            post("/api/v1.1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.path").value("/api/v1.1/products"));
  }

  @Test
  void deleteIsIdempotentForNonExisting() throws Exception {
    String id = UUID.randomUUID().toString();

    mvc.perform(delete("/api/v1.1/products/{id}", id)).andExpect(status().isNoContent());
  }
}
