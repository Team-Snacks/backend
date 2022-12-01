package com.snacks.backend.jwt.auth;


import com.snacks.backend.entity.User;
import com.snacks.backend.repository.AuthRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final AuthRepository authRepository;


  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<User> user = authRepository.findByEmail(email);

    user.orElseThrow(() -> new UsernameNotFoundException("없는 이메일입니다."));
    return new CustomUserDetails(user.get());
  }


}
