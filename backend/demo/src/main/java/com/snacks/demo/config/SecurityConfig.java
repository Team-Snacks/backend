package com.snacks.demo.config;

import com.snacks.demo.jwt.JwtAuthenticationFilter;
import com.snacks.demo.jwt.JwtAuthorizationFilter;
import com.snacks.demo.redis.RedisService;
import com.snacks.demo.repository.AuthRepository;
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

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    HttpSecurity httpSecurity = http.csrf().disable()
        .httpBasic().disable()
        .formLogin().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(corsConfig.corsFilter())
        .addFilter(new JwtAuthenticationFilter(authenticationManager(), redisService))
        .addFilter(new JwtAuthorizationFilter(authenticationManager(), authRepository))
        .authorizeRequests()
        .antMatchers("/users/**").authenticated()
        .anyRequest().permitAll().and();

  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
