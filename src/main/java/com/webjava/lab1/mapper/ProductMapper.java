package com.webjava.lab1.mapper;

import com.webjava.lab1.domain.Product;
import com.webjava.lab1.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "category", source = "categoryName")
  ProductDTO toDto(Product product);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "categoryId", ignore = true)
  @Mapping(target = "categoryName", source = "category")
  Product toEntity(ProductDTO dto);
}
