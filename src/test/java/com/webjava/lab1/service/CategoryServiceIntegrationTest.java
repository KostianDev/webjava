package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.entity.CategoryEntity;
import com.webjava.lab1.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CategoryServiceIntegrationTest extends AbstractIntegrationTest {

  @Autowired private CategoryService categoryService;
  @Autowired private CategoryRepository categoryRepository;

  @BeforeEach
  void setUp() {
    categoryRepository.deleteAll();
  }

  @Test
  @DisplayName("Should create category")
  void shouldCreateCategory() {
    CategoryEntity category =
        CategoryEntity.builder()
            .name("Cosmic Electronics")
            .description("Electronics from space")
            .build();

    CategoryEntity saved = categoryService.create(category);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("Cosmic Electronics");
  }

  @Test
  @DisplayName("Should find all categories")
  void shouldFindAllCategories() {
    categoryService.create(CategoryEntity.builder().name("Category 1").build());
    categoryService.create(CategoryEntity.builder().name("Category 2").build());

    assertThat(categoryService.findAll()).hasSize(2);
  }

  @Test
  @DisplayName("Should find category by id")
  void shouldFindCategoryById() {
    CategoryEntity saved =
        categoryService.create(CategoryEntity.builder().name("Test Category").build());

    assertThat(categoryService.findById(saved.getId()))
        .isPresent()
        .hasValueSatisfying(c -> assertThat(c.getName()).isEqualTo("Test Category"));
  }

  @Test
  @DisplayName("Should return empty when category not found")
  void shouldReturnEmptyWhenNotFound() {
    assertThat(categoryService.findById(999L)).isEmpty();
  }

  @Test
  @DisplayName("Should find category by name")
  void shouldFindCategoryByName() {
    categoryService.create(CategoryEntity.builder().name("Unique Name").build());

    assertThat(categoryService.findByName("Unique Name")).isPresent();
    assertThat(categoryService.findByName("Non-existent")).isEmpty();
  }

  @Test
  @DisplayName("Should update category")
  void shouldUpdateCategory() {
    CategoryEntity saved =
        categoryService.create(
            CategoryEntity.builder().name("Original").description("Original desc").build());

    CategoryEntity updated =
        categoryService.update(
            saved.getId(),
            CategoryEntity.builder().name("Updated").description("Updated desc").build());

    assertThat(updated.getName()).isEqualTo("Updated");
    assertThat(updated.getDescription()).isEqualTo("Updated desc");
  }

  @Test
  @DisplayName("Should throw exception when updating non-existent category")
  void shouldThrowWhenUpdatingNonExistent() {
    assertThatThrownBy(
            () -> categoryService.update(999L, CategoryEntity.builder().name("Test").build()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Category")
        .hasMessageContaining("999");
  }

  @Test
  @DisplayName("Should delete category")
  void shouldDeleteCategory() {
    CategoryEntity saved =
        categoryService.create(CategoryEntity.builder().name("To Delete").build());

    categoryService.delete(saved.getId());

    assertThat(categoryService.findById(saved.getId())).isEmpty();
  }

  @Test
  @DisplayName("Should throw NullPointerException when id is null")
  void shouldThrowWhenIdIsNull() {
    assertThatThrownBy(() -> categoryService.findById(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("must not be null");
  }
}
