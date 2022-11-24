package com.snacks.demo.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacks.demo.dto.UserDto;
import com.snacks.demo.jwt.auth.CustomUserDetails;
import com.snacks.demo.response.ResponseService;
import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
    setFilterProcessesUrl("/auth/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

//    System.out.println("@ attempt" + request.getPathInfo() + request.getMethod());
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

    String jwtToken = JWT.create()
        .withSubject(customUserDetails.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
        .withClaim("email", customUserDetails.getUsername())
        .sign(Algorithm.HMAC512(JwtProperties.SECRET));

    response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

    ResponseService responseService = new ResponseService();
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(response.getWriter(), responseService.getCommonResponse());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
