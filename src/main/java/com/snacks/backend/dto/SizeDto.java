package com.snacks.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 위젯의 크기 정보 dto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SizeDto {
  //위젯의 너비
  private Integer w;
  //위젯의 높이
  private  Integer h;
}
