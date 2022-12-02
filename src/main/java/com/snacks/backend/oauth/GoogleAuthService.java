package com.snacks.backend.oauth;

import com.snacks.backend.dto.AuthRequest;
import com.snacks.backend.entity.User;
import com.snacks.backend.jwt.JwtProvider;
import com.snacks.backend.oauth.client.ClientGoogle;
import com.snacks.backend.redis.RedisService;
import com.snacks.backend.repository.AuthRepository;
import com.snacks.backend.response.ConstantResponse;
import com.snacks.backend.response.ResponseService;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService {
  @Autowired
  private ClientGoogle clientGoogle;

  @Autowired
  private AuthRepository authRepository;
  @Autowired
  private JwtProvider jwtProvider;

  @Autowired
  private RedisService redisService;
  @Autowired
  private ResponseService responseService;

  //V2를 위해 작성
  @Transactional
  public ResponseEntity login(AuthRequest authRequest){
    User googleUser = clientGoogle.getUserData(authRequest.getAccessToken());
    String email = googleUser.getEmail();
    Optional<User> user = authRepository.findByEmail(email);

    String refreshToken = jwtProvider.createToken(email, "refresh");
    String accessToken = jwtProvider.createToken(email, "access");

    redisService.setValues(email, refreshToken);

    if (user.isEmpty())
      authRepository.save(googleUser);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.getTokenResponse(refreshToken, accessToken));

  }

}
