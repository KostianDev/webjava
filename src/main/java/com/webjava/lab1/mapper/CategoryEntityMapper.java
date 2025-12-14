package com.webjava.lab1.mapper;

import com.webjava.lab1.domain.Category;
import com.webjava.lab1.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityMapper {

  public Category toDomain(CategoryEntity entity) {
    if (entity == null) {
      return null;
    }
    return Category.builder()
        .id(entity.getId())
        .name(entity.getName())
        .description(entity.getDescription())
        .build();
  }

  public CategoryEntity toEntity(Category domain) {
    if (domain == null) {
      return null;
    }
    return CategoryEntity.builder()
        .id(domain.getId())
        .name(domain.getName())
        .description(domain.getDescription())
        .build();
  }
}
