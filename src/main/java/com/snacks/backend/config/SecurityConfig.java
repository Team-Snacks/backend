package com.snacks.backend.config;

import com.snacks.backend.jwt.JwtAuthenticationFilter;
import com.snacks.backend.jwt.JwtAuthorizationFilter;
import com.snacks.backend.jwt.JwtProvider;
import com.snacks.backend.oauth.CustomOAuth2UserService;
import com.snacks.backend.oauth.OAuth2SuccessHandler;
import com.snacks.backend.redis.RedisService;
import com.snacks.backend.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity()
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private CorsConfig corsConfig;

  @Autowired
  private AuthRepository authRepository;

  @Autowired
  private RedisService redisService;

  @Autowired
  private JwtProvider jwtProvider;

  @Autowired
  private CustomOAuth2UserService customOAuth2UserService;

  @Autowired
  private OAuth2SuccessHandler oAuth2SuccessHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    HttpSecurity httpSecurity = http.csrf().disable()
        .httpBasic().disable()
        .formLogin().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(corsConfig.corsFilter())
        .addFilter(new JwtAuthenticationFilter(authenticationManager(), redisService, jwtProvider))
        .addFilter(new JwtAuthorizationFilter(authenticationManager(), authRepository, jwtProvider))
        .authorizeRequests()
        .antMatchers("/users/**").authenticated()
        .anyRequest().permitAll().and();


    httpSecurity.oauth2Login()
        .userInfoEndpoint().userService(customOAuth2UserService)
        .and()
        .successHandler(oAuth2SuccessHandler)
        .permitAll();

  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
