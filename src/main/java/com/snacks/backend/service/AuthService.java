package com.snacks.backend.service;

import com.snacks.backend.dto.Role;
import com.snacks.backend.dto.UserDto;
import com.snacks.backend.entity.User;
import com.snacks.backend.repository.AuthRepository;
import com.snacks.backend.response.ConstantResponse;
import com.snacks.backend.response.ResponseService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private AuthRepository authRepository;
  private ResponseService responseService;

  @Autowired
  PasswordEncoder passwordEncoder;

  public AuthService(AuthRepository authRepository, ResponseService responseService) {
    this.authRepository = authRepository;
    this.responseService = responseService;
  }


  public ResponseEntity signUp(UserDto userDto) {
    //signup
    User user = new User();
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    user.setRole(Role.USER);
    user.setProvider("local");

    Optional<User> existedUser = authRepository.findByEmailAndProvider(user.getEmail(), "local");

    //이미 회원가입한 유저인 경우
    if (existedUser.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(responseService.errorResponse(ConstantResponse.EMAIL_EXSIST));
    }
    authRepository.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).
        body(responseService.getCommonResponse());
  }

}
