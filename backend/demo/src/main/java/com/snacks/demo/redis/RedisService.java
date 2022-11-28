package com.snacks.demo.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate redisTemplate;

  public void setValues(String email, String token){
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    values.set(email, token, Duration.ofMinutes(1)); //1분 뒤 삭제
  }

  public String getValues(String email){
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    return values.get(email);
  }
}
