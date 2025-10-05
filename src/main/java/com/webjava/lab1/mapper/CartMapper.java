package com.webjava.lab1.mapper;

import com.webjava.lab1.domain.Cart;
import com.webjava.lab1.domain.CartItem;
import com.webjava.lab1.dto.CartDTO;
import com.webjava.lab1.dto.CartItemDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
  CartDTO toDto(Cart cart);

  Cart toEntity(CartDTO dto);

  CartItemDTO toDto(CartItem item);

  CartItem toEntity(CartItemDTO dto);

  List<CartDTO> toDtoList(List<Cart> carts);
}
