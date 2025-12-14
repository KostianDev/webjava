package com.webjava.lab1.repository;

import com.webjava.lab1.entity.ProductEntity;
import com.webjava.lab1.projection.ProductSalesReport;
import com.webjava.lab1.projection.ProductSummary;
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

  @Query(
      """
      SELECT p.id AS productId, p.name AS productName, c.name AS categoryName,
             SUM(oi.quantity) AS totalQuantitySold, SUM(oi.quantity * oi.price) AS totalRevenue
      FROM OrderItemEntity oi
      JOIN oi.product p
      JOIN p.category c
      GROUP BY p.id, p.name, c.name
      ORDER BY totalQuantitySold DESC
      """)
  List<ProductSalesReport> findTopSellingProducts();

  @Query(
      """
      SELECT p.id AS productId, p.name AS productName, c.name AS categoryName,
             SUM(oi.quantity) AS totalQuantitySold, SUM(oi.quantity * oi.price) AS totalRevenue
      FROM OrderItemEntity oi
      JOIN oi.product p
      JOIN p.category c
      WHERE c.id = :categoryId
      GROUP BY p.id, p.name, c.name
      ORDER BY totalQuantitySold DESC
      """)
  List<ProductSalesReport> findTopSellingProductsByCategory(@Param("categoryId") Long categoryId);

  @Query(
      """
      SELECT p.id AS id, p.name AS name, p.price AS price, c.name AS categoryName
      FROM ProductEntity p
      JOIN p.category c
      WHERE p.price <= :maxPrice
      ORDER BY p.price ASC
      """)
  List<ProductSummary> findAffordableProducts(@Param("maxPrice") java.math.BigDecimal maxPrice);
}
