package com.snacks.backend.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.snacks.backend.config.EnvConfiguration;
import com.snacks.backend.dto.UserDto;
import com.snacks.backend.entity.User;
import com.snacks.backend.jwt.JwtProvider;
import com.snacks.backend.redis.RedisService;
import com.snacks.backend.repository.AuthRepository;
import com.snacks.backend.response.ResponseService;
import com.snacks.backend.service.AuthService;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  AuthService authService;

  @Autowired
  ResponseService responseService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  EnvConfiguration env;

  @Autowired
  RedisService redisService;

  @Autowired
  JwtProvider jwtProvider;

  @Autowired
  AuthRepository authRepository;

  /**
   * 회원가입을 진행
   * @param userDto 유저 정보
   * @param bindingResult 검증 오류를 담고있는 객체
   * @return 상태 코드 및 메시지
   */
  @PostMapping("")
  public ResponseEntity signUp(@Validated @RequestBody UserDto userDto,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest()
          .body(responseService.errorResponse(bindingResult.getFieldError().getDefaultMessage()));
    }
    return authService.signUp(userDto);
  }


  /**
   * refresh 토큰을 재발급
   * @param request
   * @param response
   * @return 상태 코드 및 메시지
   */
  @GetMapping("/refresh")
  public ResponseEntity refresh(HttpServletRequest request, HttpServletResponse response) {
    try {
      String refreshtoken = request.getHeader("Authorization")
          .replace("Bearer ", "");

      if (refreshtoken == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(responseService.errorResponse("JWT 토큰이 존재하지 않습니다."));
      }

      if (jwtProvider.verifyToken(refreshtoken) == false) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(responseService.errorResponse("잘못된 JWT 토큰입니다."));
      }

      String provider = jwtProvider.getProvider(refreshtoken);
      String email = jwtProvider.getEmail(refreshtoken);

      //로컬 로그인일 경우
      if (provider.equals("local")) {
        Optional<User> user = authRepository.findByEmailAndProvider(email, "local");
        Long id = user.get().getId();
        String value = redisService.getValues(id.toString());
        if (!value.equals(refreshtoken)) {
          ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(responseService.errorResponse("refresh 토큰이 다릅니다."));
        }
      }
      //구글 로그인일 경우
      else {
        String value = redisService.getValues(email);
        if (!value.equals(refreshtoken)){
          ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(responseService.errorResponse("refresh 토큰이 다릅니다."));
        }
      }

      String accessToken = jwtProvider.createToken(jwtProvider.getEmail(refreshtoken), "access", provider);

      response.addHeader(env.getProperty("access_token"),
          env.getProperty("token_prefix") + accessToken);
      return ResponseEntity.status(HttpStatus.OK).
          body(responseService.getCommonResponse());
    } catch (TokenExpiredException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(responseService.errorResponse("refresh 토큰이 만료 되었습니다. 다시 로그인하세요"));

    }
  }
}

