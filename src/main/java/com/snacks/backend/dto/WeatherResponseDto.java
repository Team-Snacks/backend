package com.snacks.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherResponseDto {
    private String category;
    //private Integer fcstValue;
    private Number fcstValue;
}
