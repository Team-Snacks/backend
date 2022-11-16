package com.snacks.demo.repository;

import com.snacks.demo.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  User save(User user);

}
