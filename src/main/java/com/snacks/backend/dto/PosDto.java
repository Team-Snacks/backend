package com.snacks.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 위젯의 위치 정보 dto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosDto
{
  // x 좌표값
  private Integer x;
  // y 좌표값
  private Integer y;
}
