package com.snacks.backend.oauth.client;

import com.snacks.backend.entity.User;

//V2를 위해 작성
public interface ClientProxy {
  User getUserData(String accessToken);
}
