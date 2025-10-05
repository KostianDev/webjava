package com.webjava.lab1.mapper;

import com.webjava.lab1.domain.Order;
import com.webjava.lab1.domain.OrderItem;
import com.webjava.lab1.dto.OrderDTO;
import com.webjava.lab1.dto.OrderItemDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDto(Order order);
    Order toEntity(OrderDTO dto);
    OrderItemDTO toDto(OrderItem item);
    OrderItem toEntity(OrderItemDTO dto);
    List<OrderDTO> toDtoList(List<Order> orders);
}
