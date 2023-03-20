package com.snacks.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 위젯 dto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WidgetDto {
  //위젯 이름
  private String name;
  //위젯 설명
  private String description;
  //위젯 이미지
  private String image;
}
