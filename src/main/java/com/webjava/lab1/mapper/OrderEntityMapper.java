package com.webjava.lab1.mapper;

import com.webjava.lab1.domain.Order;
import com.webjava.lab1.domain.OrderItem;
import com.webjava.lab1.entity.OrderEntity;
import com.webjava.lab1.entity.OrderItemEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderEntityMapper {

  public Order toDomain(OrderEntity entity) {
    if (entity == null) {
      return null;
    }
    return Order.builder()
        .id(entity.getId())
        .orderNumber(entity.getOrderNumber())
        .userId(entity.getUser() != null ? entity.getUser().getId() : null)
        .items(mapItemsToDomain(entity.getItems()))
        .total(entity.getTotal())
        .createdAt(entity.getCreatedAt())
        .build();
  }

  private List<OrderItem> mapItemsToDomain(List<OrderItemEntity> entities) {
    if (entities == null) {
      return List.of();
    }
    return entities.stream().map(this::toOrderItemDomain).collect(Collectors.toList());
  }

  private OrderItem toOrderItemDomain(OrderItemEntity entity) {
    return OrderItem.builder()
        .id(entity.getId())
        .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
        .productName(entity.getProduct() != null ? entity.getProduct().getName() : null)
        .quantity(entity.getQuantity())
        .price(entity.getPrice())
        .build();
  }
}
