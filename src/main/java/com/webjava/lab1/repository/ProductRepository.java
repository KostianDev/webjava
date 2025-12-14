package com.webjava.lab1.repository;

import com.webjava.lab1.entity.ProductEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

  List<ProductEntity> findByNameContainingIgnoreCase(String name);

  List<ProductEntity> findByCategoryId(Long categoryId);

  Optional<ProductEntity> findByNameAndCategoryId(String name, Long categoryId);

  boolean existsByNameAndCategoryId(String name, Long categoryId);

  @Query("SELECT p FROM ProductEntity p WHERE p.price >= :minPrice AND p.price <= :maxPrice")
  List<ProductEntity> findByPriceRange(
      @Param("minPrice") java.math.BigDecimal minPrice,
      @Param("maxPrice") java.math.BigDecimal maxPrice);
}
