package com.snacks.demo.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.snacks.demo.config.EnvConfiguration;
import com.snacks.demo.entity.User;
import com.snacks.demo.jwt.auth.CustomUserDetails;
import com.snacks.demo.repository.AuthRepository;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private AuthRepository authRepository;

  @Autowired
  EnvConfiguration env;

  public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
      AuthRepository authRepository) {
    super(authenticationManager);
    this.authRepository = authRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    String header = request.getHeader(env.getProperty("header_string"));
    if (header == null || !header.startsWith(env.getProperty("token_prefix"))) {
      chain.doFilter(request, response);
      return;
    }
    String token = request.getHeader(env.getProperty("header_string"))
        .replace(env.getProperty("token_prefix"), "");

    String email = JWT.require(Algorithm.HMAC512(env.getProperty("secret"))).build().verify(token)
        .getClaim("email").asString();

    if (email != null) {
      Optional<User> user = authRepository.findByEmail(email);
      if (user.isEmpty()) {
        chain.doFilter(request, response);
      }

      CustomUserDetails customUserDetails = new CustomUserDetails(user.get());
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(
              customUserDetails,
              null,
              customUserDetails.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(authentication);

    }
    chain.doFilter(request, response);
  }
}
