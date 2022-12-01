package com.snacks.backend.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacks.backend.config.EnvConfiguration;
import com.snacks.backend.dto.UserDto;
import com.snacks.backend.jwt.auth.CustomUserDetails;
import com.snacks.backend.redis.RedisService;
import com.snacks.backend.response.ResponseService;
import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  private final RedisService redisService;

  @Autowired
  EnvConfiguration env;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
      RedisService redisService) {
    this.authenticationManager = authenticationManager;
    this.redisService = redisService;
    setFilterProcessesUrl("/auth/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    ObjectMapper om = new ObjectMapper();
    UserDto userDto = null;
    try {
      userDto = om.readValue(request.getInputStream(), UserDto.class);
    } catch (Exception e) {
      e.printStackTrace();
    }

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        userDto.getEmail(), userDto.getPassword());

    Authentication authentication = null;
    try {
      authentication = authenticationManager.authenticate(authenticationToken);
    } catch (Exception e) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      ResponseService responseService = new ResponseService();
      ObjectMapper mapper = new ObjectMapper();
      try {
        mapper.writeValue(response.getWriter(), responseService.errorResponse("로그인에 실패했습니다."));
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    return authentication;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {

    CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

    String refreshToken = JWT.create()
        .withSubject(customUserDetails.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(
            env.getProperty("REFRESH_EXPR"))))
        .withClaim("email", customUserDetails.getUsername())
        .sign(Algorithm.HMAC512(env.getProperty("SECRET_SALT")));

    String accessToken = JWT.create()
        .withSubject(customUserDetails.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(
            env.getProperty("ACCESS_EXPR"))))
        .withClaim("email", customUserDetails.getUsername())
        .sign(Algorithm.HMAC512(env.getProperty("SECRET_SALT")));

    redisService.setValues(customUserDetails.getUsername(), refreshToken);

    response.addHeader("Authorization",
        "Bearer " + accessToken);

    ResponseService responseService = new ResponseService();
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(response.getWriter(),
          responseService.getTokenResponse("Bearer " + refreshToken,
              "Bearer " + accessToken));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
