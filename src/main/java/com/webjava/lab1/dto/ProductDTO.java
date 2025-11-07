package com.webjava.lab1.dto;

import com.webjava.lab1.validation.CosmicWordCheck;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
  private UUID id;

  @NotBlank(message = "name must not be blank")
  @CosmicWordCheck
  private String name;

  private String description;

  @NotNull(message = "price must not be null")
  @DecimalMin(value = "0.01", message = "price must be greater than 0")
  private BigDecimal price;

  @NotBlank(message = "category must not be blank")
  private String category;
}
