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
    http.csrf().disable()
        .httpBasic().disable()
        .formLogin().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(corsConfig.corsFilter())
        .addFilter(new JwtAuthenticationFilter(authenticationManager(), redisService, jwtProvider))
        .addFilter(new JwtAuthorizationFilter(authenticationManager(), authRepository))
        .authorizeRequests()
        .antMatchers("/users/**").authenticated();
        /*
        추후 인가가 필요한 API가 추가되면 이런 식으로 사용하시면 될 것 같습니다.
        .antMatchers("/users/example1").hasRole("USER")
        .andMatchers("/users/example2").hasRole("ADMIN")
        .authenticated()
        */

    http.oauth2Login()
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
