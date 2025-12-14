package com.webjava.lab1.mapper;

import com.webjava.lab1.domain.Product;
import com.webjava.lab1.entity.CategoryEntity;
import com.webjava.lab1.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {

  public Product toDomain(ProductEntity entity) {
    if (entity == null) {
      return null;
    }
    return Product.builder()
        .id(entity.getId())
        .name(entity.getName())
        .description(entity.getDescription())
        .price(entity.getPrice())
        .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
        .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
        .build();
  }

  public ProductEntity toEntity(Product domain, CategoryEntity category) {
    if (domain == null) {
      return null;
    }
    return ProductEntity.builder()
        .id(domain.getId())
        .name(domain.getName())
        .description(domain.getDescription())
        .price(domain.getPrice())
        .category(category)
        .build();
  }
}
