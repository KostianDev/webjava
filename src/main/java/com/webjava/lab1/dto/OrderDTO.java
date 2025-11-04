package com.webjava.lab1.dto;

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
public class OrderDTO {

  private UUID id;
  private List<OrderItemDTO> items = new ArrayList<>();
  private BigDecimal total;
}
