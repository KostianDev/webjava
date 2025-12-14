package com.webjava.lab1.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

  private Long id;
  private Long productId;
  private String productName;
  private int quantity;
  private BigDecimal price;
}
