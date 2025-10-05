package com.webjava.lab1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webjava.lab1.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void createProductHappyPath() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Yarn");
        dto.setDescription("Antigravity yarn for space knitting");
        dto.setPrice(new BigDecimal("9.99"));
        dto.setCategory("Textiles");

        mvc.perform(post("/api/products")
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

        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations").isArray())
                .andExpect(jsonPath("$.violations[0].field").exists());
    }
}
