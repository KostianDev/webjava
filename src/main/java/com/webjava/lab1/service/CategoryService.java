package com.webjava.lab1.service;

import com.webjava.lab1.entity.CategoryEntity;
import com.webjava.lab1.repository.CategoryRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Transactional
  public CategoryEntity create(CategoryEntity category) {
    Objects.requireNonNull(category, "category must not be null");
    return categoryRepository.save(category);
  }

  @Transactional(readOnly = true)
  public List<CategoryEntity> findAll() {
    return categoryRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<CategoryEntity> findById(Long id) {
    Objects.requireNonNull(id, "id must not be null");
    return categoryRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public Optional<CategoryEntity> findByName(String name) {
    return categoryRepository.findByName(name);
  }

  @Transactional
  public CategoryEntity update(Long id, CategoryEntity category) {
    Objects.requireNonNull(id, "id must not be null");
    CategoryEntity existing =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Category", id));
    existing.setName(category.getName());
    existing.setDescription(category.getDescription());
    return categoryRepository.save(existing);
  }

  @Transactional
  public void delete(Long id) {
    Objects.requireNonNull(id, "id must not be null");
    categoryRepository.deleteById(id);
  }
}
