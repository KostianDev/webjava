package com.webjava.lab1.repository;

import com.webjava.lab1.entity.OrderItemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

  List<OrderItemEntity> findByOrderId(Long orderId);

  List<OrderItemEntity> findByProductId(Long productId);
}
