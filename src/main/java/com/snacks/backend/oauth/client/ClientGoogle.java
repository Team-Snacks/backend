package com.snacks.backend.oauth.client;

import com.snacks.backend.dto.Role;
import com.snacks.backend.entity.User;
import com.snacks.backend.jwt.exception.TokenValidFailedException;
import com.snacks.backend.oauth.dto.GoogleUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


//V2를 위해 작성

@Component
@RequiredArgsConstructor
public class ClientGoogle implements ClientProxy{

  //private final WebClient webClient;

  @Override
  public User getUserData(String accessToken) {
    GoogleUserResponse googleUserResponse = WebClient.create().get()
        .uri("https://oauth2.googleapis.com/tokeninfo", builder -> builder.queryParam("id_token", accessToken).build())
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new TokenValidFailedException("Social Access Token is unauthorized")))
        .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new TokenValidFailedException("Internal Server Error")))
        .bodyToMono(GoogleUserResponse.class)
        .block();

    return User.builder()
        .name(googleUserResponse.getName())
        .email(googleUserResponse.getEmail())
        .picture(googleUserResponse.getPicture())
        .provider("google")
        .role(Role.USER)
        .build();

  }

}
