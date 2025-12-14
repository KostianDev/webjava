package com.webjava.lab1.service;

import com.webjava.lab1.entity.CategoryEntity;
import com.webjava.lab1.entity.ProductEntity;
import com.webjava.lab1.projection.ProductSalesReport;
import com.webjava.lab1.projection.ProductSummary;
import com.webjava.lab1.repository.CategoryRepository;
import com.webjava.lab1.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductJpaService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public ProductJpaService(
      ProductRepository productRepository, CategoryRepository categoryRepository) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
  }

  @Transactional
  public ProductEntity create(ProductEntity product, Long categoryId) {
    Objects.requireNonNull(categoryId, "categoryId must not be null");
    CategoryEntity category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));
    product.setCategory(category);
    return productRepository.save(product);
  }

  @Transactional(readOnly = true)
  public List<ProductEntity> findAll() {
    return productRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<ProductEntity> findById(Long id) {
    Objects.requireNonNull(id, "id must not be null");
    return productRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<ProductEntity> findByCategory(Long categoryId) {
    return productRepository.findByCategoryId(categoryId);
  }

  @Transactional(readOnly = true)
  public List<ProductEntity> searchByName(String name) {
    return productRepository.findByNameContainingIgnoreCase(name);
  }

  @Transactional(readOnly = true)
  public List<ProductEntity> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
    return productRepository.findByPriceRange(minPrice, maxPrice);
  }

  @Transactional
  public ProductEntity update(Long id, ProductEntity product, Long categoryId) {
    Objects.requireNonNull(id, "id must not be null");
    ProductEntity existing =
        productRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product", id));

    existing.setName(product.getName());
    existing.setDescription(product.getDescription());
    existing.setPrice(product.getPrice());

    if (categoryId != null && !categoryId.equals(existing.getCategory().getId())) {
      CategoryEntity category =
          categoryRepository
              .findById(categoryId)
              .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));
      existing.setCategory(category);
    }

    return productRepository.save(existing);
  }

  @Transactional
  public void delete(Long id) {
    Objects.requireNonNull(id, "id must not be null");
    productRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public List<ProductSalesReport> getTopSellingProducts() {
    return productRepository.findTopSellingProducts();
  }

  @Transactional(readOnly = true)
  public List<ProductSalesReport> getTopSellingProductsByCategory(Long categoryId) {
    return productRepository.findTopSellingProductsByCategory(categoryId);
  }

  @Transactional(readOnly = true)
  public List<ProductSummary> getAffordableProducts(BigDecimal maxPrice) {
    return productRepository.findAffordableProducts(maxPrice);
  }
}
