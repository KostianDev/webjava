package com.webjava.lab1.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  private UUID id;
  private List<OrderItem> items = new ArrayList<>();
  private BigDecimal total;
}
