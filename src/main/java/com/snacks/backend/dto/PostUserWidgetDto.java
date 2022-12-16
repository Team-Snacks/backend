package com.snacks.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUserWidgetDto {
  private String name;
  private PosDto pos;
  private SizeDto size;
  private String data;
}
