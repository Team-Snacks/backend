package com.snacks.backend.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.snacks.backend.config.EnvConfiguration;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
  @Autowired
  EnvConfiguration env;

  public String createToken(String subject, String type)
  {
    String expr;
    if (type == "refresh")
      expr = env.getProperty("REFRESH_EXPR");
    else
      expr = env.getProperty("ACCESS_EXPR");

     return JWT.create()
        .withSubject(subject)
        .withExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(expr)))
        .withClaim("email", subject)
        .sign(Algorithm.HMAC512(env.getProperty("SECRET_SALT")));
  }
}
