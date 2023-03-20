package com.snacks.backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 유저의 위젯을 추가하기 위한 dto
 */
@Getter
@Setter
public class PostUserWidgetDto {
  //위젯의 이름
  private String name;
  //위젯의 위치
  private PosDto pos;
  //위젯의 크기
  private SizeDto size;
  //위젯의 데이터 값
  private String data;
}
