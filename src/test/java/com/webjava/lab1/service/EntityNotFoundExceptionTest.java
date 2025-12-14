package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityNotFoundExceptionTest {

  @Test
  @DisplayName("Should create exception with entity name and id")
  void shouldCreateExceptionWithEntityNameAndId() {
    EntityNotFoundException exception = new EntityNotFoundException("User", 123L);

    assertThat(exception.getMessage()).isEqualTo("User with id 123 not found");
    assertThat(exception.getEntityName()).isEqualTo("User");
    assertThat(exception.getEntityId()).isEqualTo(123L);
  }

  @Test
  @DisplayName("Should handle string id")
  void shouldHandleStringId() {
    EntityNotFoundException exception = new EntityNotFoundException("Order", "ORD-12345");

    assertThat(exception.getMessage()).isEqualTo("Order with id ORD-12345 not found");
    assertThat(exception.getEntityName()).isEqualTo("Order");
    assertThat(exception.getEntityId()).isEqualTo("ORD-12345");
  }
}
