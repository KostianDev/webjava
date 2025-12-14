package com.webjava.lab1.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  private Long id;
  private String orderNumber;
  private Long userId;
  @Builder.Default private List<OrderItem> items = new ArrayList<>();
  private BigDecimal total;
  private LocalDateTime createdAt;
}
