package com.snacks.backend.entity;

import com.snacks.backend.dto.Role;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "email")
 // @NotNull
  private String email;

  @Column(name = "password")
  //@NotNull
  private String password;

  @Column(name = "name", nullable = true)
  private String name;

  @Column(name = "picture", length = 512, nullable = true)
  private String picture;

  @Column(name = "provider_type", length = 20, nullable = true)
  private String provider;

  @Column(name = "role", length = 20, nullable = true)
  private Role role;

  @Builder
  public User(String name, String email, String picture, String provider, Role role) {
    this.name = name;
    this.email = email;
    this.picture = picture;
    this.provider = provider;
    this.role = role;
  }
public User update(String name, String picture) {
    this.name = name;
    this.picture = picture;
    return this;
}
  public String getRoleKey() {
    return this.role.getKey();
  }
}
