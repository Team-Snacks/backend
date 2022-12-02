package com.snacks.backend.repository;

import com.snacks.backend.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  //oauth 추가
  Optional<User> findByEmailAndProvider(String email, String provider);
  User save(User user);

}
