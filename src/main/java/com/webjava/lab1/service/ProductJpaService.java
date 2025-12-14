package com.webjava.lab1.service;

import com.webjava.lab1.domain.Product;
import com.webjava.lab1.entity.CategoryEntity;
import com.webjava.lab1.entity.ProductEntity;
import com.webjava.lab1.mapper.ProductEntityMapper;
import com.webjava.lab1.projection.ProductSalesReport;
import com.webjava.lab1.projection.ProductSummary;
import com.webjava.lab1.repository.CategoryRepository;
import com.webjava.lab1.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductJpaService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductEntityMapper mapper;

  public ProductJpaService(
      ProductRepository productRepository,
      CategoryRepository categoryRepository,
      ProductEntityMapper mapper) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.mapper = mapper;
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_write')")
  public Product create(Product product) {
    Long categoryId =
        Objects.requireNonNull(product.getCategoryId(), "categoryId must not be null");
    CategoryEntity category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));
    ProductEntity entity = mapper.toEntity(product, category);
    ProductEntity saved = productRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasAuthority('SCOPE_read')")
  public List<Product> findAll() {
    return productRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasAuthority('SCOPE_read')")
  public Optional<Product> findById(Long id) {
    Objects.requireNonNull(id, "id must not be null");
    return productRepository.findById(id).map(mapper::toDomain);
  }

  @Transactional(readOnly = true)
  public List<Product> findByCategory(Long categoryId) {
    return productRepository.findByCategoryId(categoryId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<Product> searchByName(String name) {
    return productRepository.findByNameContainingIgnoreCase(name).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
    return productRepository.findByPriceRange(minPrice, maxPrice).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_write')")
  public Product update(Long id, Product product) {
    Objects.requireNonNull(id, "id must not be null");
    ProductEntity existing =
        productRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product", id));

    existing.setName(product.getName());
    existing.setDescription(product.getDescription());
    existing.setPrice(product.getPrice());

    Long categoryId = product.getCategoryId();
    if (categoryId != null && !categoryId.equals(existing.getCategory().getId())) {
      CategoryEntity category =
          categoryRepository
              .findById(categoryId)
              .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));
      existing.setCategory(category);
    }

    ProductEntity saved = productRepository.save(existing);
    return mapper.toDomain(saved);
  }

  @Transactional
  @PreAuthorize("hasAuthority('SCOPE_write')")
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
