package com.snacks.backend.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.snacks.backend.config.EnvConfiguration;
import com.snacks.backend.redis.RedisService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
  @Autowired
  EnvConfiguration env;

  @Autowired
  RedisService redisService;

  public String createToken(String subject, String type, String provider)
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
         .withClaim("provider", provider)
        .sign(Algorithm.HMAC512(env.getProperty("SECRET_SALT")));
  }

  public boolean verifyToken(String token)
  {
    DecodedJWT decodedJWT = getdecodedJwt(token);
    String email = decodedJWT.getSubject();

    //프론트에서 받아온 refresh 토큰과 redis에 있는 refresh 토큰 비교
    if (!token.equals(redisService.getValues(email))) {
        return false;
    }
    return true;
  }

  public DecodedJWT getdecodedJwt(String token)
  {
    JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC512(env.getProperty("SECRET_SALT"))).build();
    return jwtVerifier.verify(token);

  }
  public String getEmail(String token)
  {
    DecodedJWT decodedJWT = getdecodedJwt(token);
    return decodedJWT.getSubject();
  }

  public String getProvider(String token)
  {
    DecodedJWT decodedJWT = getdecodedJwt(token);
    return decodedJWT.getClaim("provider").asString();
  }
}
