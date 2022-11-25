package com.snacks.demo.config;

import com.snacks.demo.jwt.CustomFilter;
import com.snacks.demo.jwt.CustomFilter2;
import com.snacks.demo.jwt.JwtAuthenticationFilter;
import com.snacks.demo.jwt.JwtAuthorizationFilter;
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
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration
//@EnableWebSecurity(debug = true)
@EnableWebSecurity()
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private CorsConfig corsConfig;

  @Autowired
  private AuthRepository authRepository;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    HttpSecurity httpSecurity = http.csrf().disable()
        .httpBasic().disable()
        .formLogin().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(corsConfig.corsFilter())
        .addFilter(new JwtAuthenticationFilter(authenticationManager()))
        .addFilter(new JwtAuthorizationFilter(authenticationManager(), authRepository))
        //.addFilterAfter(new CustomFilter(), JwtAuthorizationFilter.class)
        //.addFilterAfter(new CustomFilter2(), FilterSecurityInterceptor.class)
        .authorizeRequests()
        .antMatchers("/users/**").authenticated()
        //.antMatchers("/auth/**").permitAll()
        .anyRequest().permitAll().and();
    //.anyRequest().authenticated().and();

  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
