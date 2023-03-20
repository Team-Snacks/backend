package com.snacks.backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 유저의 위젯 정보 dto
 */
@Getter
@Setter
public class UserWidgetDto {
  //유저의 위젯 id
  private Long duuid;
  //유저의 위젯 이름
  private String name;
  //유저의 위젯 위치
  private PosDto pos;
  //유저의 위젯 크기
  private SizeDto size;
  //유저의 위젯 데이터 값
  private String data;
}


