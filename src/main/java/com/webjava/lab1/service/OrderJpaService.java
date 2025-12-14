package com.webjava.lab1.service;

import com.webjava.lab1.entity.OrderEntity;
import com.webjava.lab1.entity.OrderItemEntity;
import com.webjava.lab1.entity.ProductEntity;
import com.webjava.lab1.entity.UserEntity;
import com.webjava.lab1.repository.OrderRepository;
import com.webjava.lab1.repository.ProductRepository;
import com.webjava.lab1.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderJpaService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  public OrderJpaService(
      OrderRepository orderRepository,
      UserRepository userRepository,
      ProductRepository productRepository) {
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
    this.productRepository = productRepository;
  }

  @Transactional
  public OrderEntity create(Long userId, List<OrderItemRequest> itemRequests) {
    Objects.requireNonNull(userId, "userId");
    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User", userId));

    OrderEntity order =
        OrderEntity.builder()
            .orderNumber(generateOrderNumber())
            .user(user)
            .total(BigDecimal.ZERO)
            .build();

    BigDecimal total = BigDecimal.ZERO;

    for (OrderItemRequest request : itemRequests) {
      Long productId = Objects.requireNonNull(request.productId(), "productId must not be null");
      ProductEntity product =
          productRepository
              .findById(productId)
              .orElseThrow(() -> new EntityNotFoundException("Product", productId));

      OrderItemEntity item =
          OrderItemEntity.builder()
              .product(product)
              .quantity(request.quantity())
              .price(product.getPrice())
              .build();

      order.addItem(item);
      total = total.add(product.getPrice().multiply(BigDecimal.valueOf(request.quantity())));
    }

    order.setTotal(total);
    return orderRepository.save(order);
  }

  @Transactional(readOnly = true)
  public List<OrderEntity> findAll() {
    return orderRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<OrderEntity> findById(Long id) {
    Objects.requireNonNull(id, "id must not be null");
    return orderRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public Optional<OrderEntity> findByOrderNumber(String orderNumber) {
    return orderRepository.findByOrderNumber(orderNumber);
  }

  @Transactional(readOnly = true)
  public List<OrderEntity> findByUser(Long userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
  }

  @Transactional
  public void delete(Long id) {
    Objects.requireNonNull(id, "id must not be null");
    orderRepository.deleteById(id);
  }

  private String generateOrderNumber() {
    return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  public record OrderItemRequest(Long productId, Integer quantity) {}
}
