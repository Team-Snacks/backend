package com.snacks.backend.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacks.backend.config.EnvConfiguration;
import com.snacks.backend.entity.User;
import com.snacks.backend.jwt.auth.CustomUserDetails;
import com.snacks.backend.repository.AuthRepository;
import com.snacks.backend.response.ResponseService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private AuthRepository authRepository;

  @Autowired
  JwtProvider jwtProvider;
  @Autowired
  EnvConfiguration env;

  public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
      AuthRepository authRepository, JwtProvider jwtProvider) {
    super(authenticationManager);
    this.authRepository = authRepository;
    this.jwtProvider = jwtProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    String servletPath = request.getServletPath();
    String header = request.getHeader("Authorization");


    //로그인, 리프레시 요청이라면 토큰 검사 안함
    if (servletPath.equals("/auth/login") || servletPath.equals("/auth/refresh")
        || servletPath.equals("/auth") || servletPath.equals("/auth/google") || servletPath.equals("") ) {
      chain.doFilter(request, response);
    }

    //토큰 값이 없거나 정상적이지 않으면?
    else if (header == null || !header.startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      ResponseService responseService = new ResponseService();
      ObjectMapper mapper = new ObjectMapper();
      try {
        mapper.writeValue(response.getWriter(),
            responseService.errorResponse("JWT 토큰이 존재하지 않습니다."));
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    } else {
      try {
        String token = request.getHeader("Authorization")
            .replace("Bearer ", "");

        DecodedJWT decodedJWT = jwtProvider.getdecodedJwt(token);

        //authetication 객체 생성
        String email = jwtProvider.getEmail(token);
        Optional<User> user = authRepository.findByEmail(email);
        CustomUserDetails customUserDetails = new CustomUserDetails(user.get());

        Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                customUserDetails,
                null,
                customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);

      } catch (TokenExpiredException expiredException) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ResponseService responseService = new ResponseService();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(),
            responseService.errorResponse("Access Token이 만료되었습니다."));

      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ResponseService responseService = new ResponseService();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), responseService.errorResponse("잘못된 JWT 토큰입니다."));
      }
    }
  }
}
