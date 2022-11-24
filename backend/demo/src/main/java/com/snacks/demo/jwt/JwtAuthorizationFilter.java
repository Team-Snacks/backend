package com.snacks.demo.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.snacks.demo.entity.User;
import com.snacks.demo.jwt.auth.CustomUserDetails;
import com.snacks.demo.repository.AuthRepository;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private AuthRepository authRepository;

  public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
      AuthRepository authRepository) {
    super(authenticationManager);
    this.authRepository = authRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

//    System.out.println("doFilterInternal");
    String header = request.getHeader(JwtProperties.HEADER_STRING);
    if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
      chain.doFilter(request, response);
//      System.out.println("@ hello" + request.getPathInfo());
      return;
    }
//    System.out.println("Header" + header);
    String token = request.getHeader(JwtProperties.HEADER_STRING)
        .replace(JwtProperties.TOKEN_PREFIX, "");

    String email = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
        .getClaim("email").asString();

    if (email != null) {
      Optional<User> user = authRepository.findByEmail(email);
      if (user.isEmpty()) {
        chain.doFilter(request, response);
      }

//      System.out.println("user" + user.get().getEmail());
      CustomUserDetails customUserDetails = new CustomUserDetails(user.get());
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(
              customUserDetails,
              null,
              customUserDetails.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(authentication);

    }
//    System.out.println("doFilterInternal fin");
    chain.doFilter(request, response);
  }
}
