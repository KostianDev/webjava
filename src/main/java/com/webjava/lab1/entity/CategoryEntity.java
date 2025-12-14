package com.webjava.lab1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_seq")
  @SequenceGenerator(
      name = "categories_seq",
      sequenceName = "categories_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "name", nullable = false, length = 100, unique = true)
  private String name;

  @Column(name = "description", length = 500)
  private String description;

  @OneToMany(mappedBy = "category")
  @Builder.Default
  private List<ProductEntity> products = new ArrayList<>();
}
