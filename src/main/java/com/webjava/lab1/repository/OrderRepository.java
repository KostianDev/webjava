package com.webjava.lab1.repository;

import com.webjava.lab1.entity.OrderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

  Optional<OrderEntity> findByOrderNumber(String orderNumber);

  List<OrderEntity> findByUserId(Long userId);

  List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

  boolean existsByOrderNumber(String orderNumber);
}
