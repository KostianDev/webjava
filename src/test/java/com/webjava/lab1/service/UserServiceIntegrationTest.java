package com.webjava.lab1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.entity.UserEntity;
import com.webjava.lab1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserServiceIntegrationTest extends AbstractIntegrationTest {

  @Autowired private UserService userService;
  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("Should create user")
  void shouldCreateUser() {
    UserEntity user =
        UserEntity.builder().name("John Cosmos").email("john.cosmos@galaxy.com").build();

    UserEntity saved = userService.create(user);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("John Cosmos");
    assertThat(saved.getEmail()).isEqualTo("john.cosmos@galaxy.com");
  }

  @Test
  @DisplayName("Should find all users")
  void shouldFindAllUsers() {
    userService.create(UserEntity.builder().name("User1").email("user1@test.com").build());
    userService.create(UserEntity.builder().name("User2").email("user2@test.com").build());

    assertThat(userService.findAll()).hasSize(2);
  }

  @Test
  @DisplayName("Should find user by id")
  void shouldFindUserById() {
    UserEntity saved =
        userService.create(
            UserEntity.builder().name("Jane Star").email("jane.star@cosmos.com").build());

    assertThat(userService.findById(saved.getId()))
        .isPresent()
        .hasValueSatisfying(u -> assertThat(u.getName()).isEqualTo("Jane Star"));
  }

  @Test
  @DisplayName("Should return empty when user not found")
  void shouldReturnEmptyWhenNotFound() {
    assertThat(userService.findById(999L)).isEmpty();
  }

  @Test
  @DisplayName("Should find user by email")
  void shouldFindUserByEmail() {
    userService.create(UserEntity.builder().name("Test User").email("unique@email.com").build());

    assertThat(userService.findByEmail("unique@email.com")).isPresent();
    assertThat(userService.findByEmail("nonexistent@email.com")).isEmpty();
  }

  @Test
  @DisplayName("Should update user")
  void shouldUpdateUser() {
    UserEntity saved =
        userService.create(
            UserEntity.builder().name("Original Name").email("original@test.com").build());

    UserEntity updated =
        userService.update(
            saved.getId(),
            UserEntity.builder().name("Updated Name").email("updated@test.com").build());

    assertThat(updated.getName()).isEqualTo("Updated Name");
    assertThat(updated.getEmail()).isEqualTo("updated@test.com");
  }

  @Test
  @DisplayName("Should throw exception when updating non-existent user")
  void shouldThrowWhenUpdatingNonExistent() {
    assertThatThrownBy(
            () ->
                userService.update(
                    999L, UserEntity.builder().name("Test User").email("test@test.com").build()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("User")
        .hasMessageContaining("999");
  }

  @Test
  @DisplayName("Should delete user")
  void shouldDeleteUser() {
    UserEntity saved =
        userService.create(UserEntity.builder().name("To Delete").email("delete@test.com").build());

    userService.delete(saved.getId());

    assertThat(userService.findById(saved.getId())).isEmpty();
  }

  @Test
  @DisplayName("Should throw NullPointerException when id is null")
  void shouldThrowWhenIdIsNull() {
    assertThatThrownBy(() -> userService.findById(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("must not be null");
  }
}
