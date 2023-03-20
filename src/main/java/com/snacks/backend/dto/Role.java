package com.snacks.backend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 유저의 역할 dto
 */
@Getter
@RequiredArgsConstructor
public enum Role {
  //일반 유저
  USER("ROLE_USER"),
  //관리자
  ADMIN("ROLE_ADMIN");

  private final String key;
}
