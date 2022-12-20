package com.snacks.backend.controller;


import com.snacks.backend.dto.WidgetDto;
import com.snacks.backend.service.WidgetService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/widgets")
public class WidgetController {

  @Autowired
  WidgetService widgetService;

  @GetMapping("")
  public WidgetDto[] getWidets() { return widgetService.getWidgets(); }
}
