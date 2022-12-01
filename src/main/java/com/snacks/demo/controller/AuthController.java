package com.snacks.backend.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.snacks.backend.config.EnvConfiguration;
import com.snacks.backend.dto.UserDto;
import com.snacks.backend.jwt.auth.CustomUserDetails;
import com.snacks.backend.redis.RedisService;
import com.snacks.backend.response.ConstantResponse;
import com.snacks.backend.response.ResponseService;
import com.snacks.backend.service.AuthService;
import java.util.Date;
import java.util.Map;
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

  @PostMapping("")
  public ResponseEntity signUp(@Validated @RequestBody UserDto userDto,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest()
          .body(responseService.errorResponse(bindingResult.getFieldError().getDefaultMessage()));
    }
    return authService.signUp(userDto);
  }


  @GetMapping("/refresh")
  public ResponseEntity refresh(HttpServletRequest request, HttpServletResponse response) {
    try {
      String header = request.getHeader(env.getProperty("header_string"));

      if (header == null || !header.startsWith(env.getProperty("token_prefix"))) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(responseService.errorResponse("JWT 토큰이 존재하지 않습니다."));

      }
      String refreshtoken = request.getHeader(env.getProperty("header_string"))
          .replace(env.getProperty("token_prefix"), "");

      JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC512(env.getProperty("secret"))).build();
      DecodedJWT decodedJWT = jwtVerifier.verify(refreshtoken);
      String email = decodedJWT.getSubject();

      //프론트에서 받아온 refresh 토큰과 redis에 있는 refresh 토큰 비교
      if (!refreshtoken.equals(redisService.getValues(email))) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(responseService.errorResponse("잘못된 JWT 토큰입니다."));

      }

      String accessToken = JWT.create()
          .withSubject(email)
          .withExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(
              env.getProperty("access_token_validation_second"))))
          .withClaim("email", email)
          .sign(Algorithm.HMAC512(env.getProperty("secret")));

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