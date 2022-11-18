package com.snacks.demo.service;

import com.snacks.demo.dto.UserDto;
import com.snacks.demo.entity.User;
import com.snacks.demo.repository.AuthRepository;
import com.snacks.demo.response.ConstantResponse;
import com.snacks.demo.response.ResponseService;
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

    Optional<User> existedUser = authRepository.findByEmail(user.getEmail());

    if (existedUser.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(responseService.errorResponse(ConstantResponse.EMAIL_EXSIST));
    }
    authRepository.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).
        body(responseService.getCommonResponse());
  }

  public ResponseEntity login(UserDto userDto) {
    //login
    Optional<User> existedUser = authRepository.findByEmail(userDto.getEmail());
    if (!existedUser.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(responseService.errorResponse(ConstantResponse.EMAIL_NOT_FOUND));
    }
    if (!passwordEncoder.matches(userDto.getPassword(), existedUser.get().getPassword())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(responseService.errorResponse(ConstantResponse.PASSWORD_NOT_MATCH));
    }
    return ResponseEntity.status(HttpStatus.OK).
        body(responseService.getCommonResponse());
  }
}
