package com.webjava.lab1.mapper;

import com.webjava.lab1.domain.Order;
import com.webjava.lab1.domain.OrderItem;
import com.webjava.lab1.dto.OrderDTO;
import com.webjava.lab1.dto.OrderItemDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
  @Mapping(target = "id", ignore = true)
  OrderDTO toDto(Order order);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orderNumber", ignore = true)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Order toEntity(OrderDTO dto);

  @Mapping(target = "productId", ignore = true)
  OrderItemDTO toDto(OrderItem item);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "productId", ignore = true)
  @Mapping(target = "productName", ignore = true)
  OrderItem toEntity(OrderItemDTO dto);

  List<OrderDTO> toDtoList(List<Order> orders);
}
