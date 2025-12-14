package com.webjava.lab1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.AbstractIntegrationTest;
import com.webjava.lab1.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("null")
class UserRepositoryTest extends AbstractIntegrationTest {

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("Should save and find user by id")
  void shouldSaveAndFindById() {
    UserEntity user = UserEntity.builder().name("John Doe").email("john.doe@cosmos.com").build();

    UserEntity saved = userRepository.save(user);

    assertThat(saved.getId()).isNotNull();
    assertThat(userRepository.findById(saved.getId()))
        .isPresent()
        .hasValueSatisfying(u -> assertThat(u.getName()).isEqualTo("John Doe"));
  }

  @Test
  @DisplayName("Should find user by email")
  void shouldFindByEmail() {
    UserEntity user =
        UserEntity.builder().name("Jane Smith").email("jane.smith@galaxy.com").build();

    userRepository.save(user);

    assertThat(userRepository.findByEmail("jane.smith@galaxy.com"))
        .isPresent()
        .hasValueSatisfying(u -> assertThat(u.getName()).isEqualTo("Jane Smith"));
  }

  @Test
  @DisplayName("Should return empty when user not found by email")
  void shouldReturnEmptyWhenNotFoundByEmail() {
    assertThat(userRepository.findByEmail("unknown@cosmos.com")).isEmpty();
  }

  @Test
  @DisplayName("Should find all users")
  void shouldFindAll() {
    userRepository.save(UserEntity.builder().name("User1").email("user1@test.com").build());
    userRepository.save(UserEntity.builder().name("User2").email("user2@test.com").build());

    assertThat(userRepository.findAll()).hasSize(2);
  }

  @Test
  @DisplayName("Should delete user")
  void shouldDeleteUser() {
    UserEntity user =
        userRepository.save(UserEntity.builder().name("ToDelete").email("delete@test.com").build());

    userRepository.deleteById(user.getId());

    assertThat(userRepository.findById(user.getId())).isEmpty();
  }

  @Test
  @DisplayName("Should update user")
  void shouldUpdateUser() {
    UserEntity user =
        userRepository.save(
            UserEntity.builder().name("Original").email("original@test.com").build());

    user.setName("Updated");
    userRepository.save(user);

    assertThat(userRepository.findById(user.getId()))
        .isPresent()
        .hasValueSatisfying(u -> assertThat(u.getName()).isEqualTo("Updated"));
  }

  @Test
  @DisplayName("Should check if email exists")
  void shouldCheckEmailExists() {
    userRepository.save(UserEntity.builder().name("Test User").email("exists@cosmos.com").build());

    assertThat(userRepository.existsByEmail("exists@cosmos.com")).isTrue();
    assertThat(userRepository.existsByEmail("notexists@cosmos.com")).isFalse();
  }
}
