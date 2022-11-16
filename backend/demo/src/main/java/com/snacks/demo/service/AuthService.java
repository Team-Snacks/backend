package com.snacks.demo.service;

import com.snacks.demo.dto.UserDto;
import com.snacks.demo.entity.User;
import com.snacks.demo.repository.AuthRepository;
import com.snacks.demo.response.CommonResponse;
import com.snacks.demo.response.ResponseService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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


  public CommonResponse signUp(UserDto userDto){
    //signup
    User user = new User();
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));

    Optional<User> existedUser = authRepository.findByEmail(user.getEmail());

    if(existedUser.isPresent()){
      return responseService.errorResponse(409, "이미 존재하는 이메일입니다.");
    }
    authRepository.save(user);
    return responseService.getCommonResponse();
  }
}
