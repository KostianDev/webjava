package com.webjava.lab1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.entity.CategoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("null")
class CategoryRepositoryTest extends AbstractIntegrationTest {

  @Autowired private CategoryRepository categoryRepository;

  @BeforeEach
  void setUp() {
    categoryRepository.deleteAll();
  }

  @Test
  @DisplayName("Should save and find category by id")
  void shouldSaveAndFindById() {
    CategoryEntity category =
        CategoryEntity.builder().name("Space Food").description("Food for space travelers").build();

    CategoryEntity saved = categoryRepository.save(category);

    assertThat(saved.getId()).isNotNull();
    assertThat(categoryRepository.findById(saved.getId()))
        .isPresent()
        .hasValueSatisfying(c -> assertThat(c.getName()).isEqualTo("Space Food"));
  }

  @Test
  @DisplayName("Should find category by name")
  void shouldFindByName() {
    CategoryEntity category =
        CategoryEntity.builder().name("Cosmic Gear").description("Equipment for cosmos").build();

    categoryRepository.save(category);

    assertThat(categoryRepository.findByName("Cosmic Gear"))
        .isPresent()
        .hasValueSatisfying(c -> assertThat(c.getDescription()).isEqualTo("Equipment for cosmos"));
  }

  @Test
  @DisplayName("Should return empty when category not found by name")
  void shouldReturnEmptyWhenNotFoundByName() {
    assertThat(categoryRepository.findByName("Non-existent")).isEmpty();
  }

  @Test
  @DisplayName("Should find all categories")
  void shouldFindAll() {
    categoryRepository.save(CategoryEntity.builder().name("Category 1").build());
    categoryRepository.save(CategoryEntity.builder().name("Category 2").build());

    assertThat(categoryRepository.findAll()).hasSize(2);
  }

  @Test
  @DisplayName("Should delete category")
  void shouldDeleteCategory() {
    CategoryEntity category =
        categoryRepository.save(CategoryEntity.builder().name("ToDelete").build());

    categoryRepository.deleteById(category.getId());

    assertThat(categoryRepository.findById(category.getId())).isEmpty();
  }

  @Test
  @DisplayName("Should update category")
  void shouldUpdateCategory() {
    CategoryEntity category =
        categoryRepository.save(
            CategoryEntity.builder().name("Original").description("Desc").build());

    category.setName("Updated");
    categoryRepository.save(category);

    assertThat(categoryRepository.findById(category.getId()))
        .isPresent()
        .hasValueSatisfying(c -> assertThat(c.getName()).isEqualTo("Updated"));
  }
}
