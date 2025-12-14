package com.webjava.lab1.service;

import com.webjava.lab1.entity.UserEntity;
import com.webjava.lab1.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public UserEntity create(UserEntity user) {
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
    }
    return userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public List<UserEntity> findAll() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<UserEntity> findById(Long id) {
    Objects.requireNonNull(id, "id must not be null");
    return userRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public Optional<UserEntity> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional
  public UserEntity update(Long id, UserEntity user) {
    Objects.requireNonNull(id, "id must not be null");
    UserEntity existing =
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));

    if (!existing.getEmail().equals(user.getEmail())
        && userRepository.existsByEmail(user.getEmail())) {
      throw new IllegalArgumentException("Email " + user.getEmail() + " is already taken");
    }

    existing.setName(user.getName());
    existing.setEmail(user.getEmail());
    return userRepository.save(existing);
  }

  @Transactional
  public void delete(Long id) {
    Objects.requireNonNull(id, "id must not be null");
    userRepository.deleteById(id);
  }
}
