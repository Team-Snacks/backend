package com.snacks.backend.controller;

import com.snacks.backend.dto.UserDto;
import com.snacks.backend.dto.UserWidgetDto;
import com.snacks.backend.dto.WidgetDto;
import com.snacks.backend.response.CommonResponse;
import com.snacks.backend.response.ListResponse;
import com.snacks.backend.response.ResponseService;
import com.snacks.backend.service.AuthService;
import com.snacks.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  UserService userService;

  @GetMapping("/test")
  public String test(){
    return "/users/test fin";
  }

  @GetMapping("/{email}/widgets")
  public UserWidgetDto[] getUserWidget(@PathVariable String email) {
    return (userService.getUserWidget(email));
  }

  /*
  @PatchMapping("/{email}/widgets")
  public CommonResponse patchUserWidget(@PathVariable String email, @RequestBody UserWidgetDto[] userWidgetDtos) {
    return (userService.patchUserWidget(email, userWidgetDtos));
  }

  @PostMapping("/widets")
  public CommonResponse PostWidget(@RequestBody WidgetDto[] widgetDtos) {
    return (userService.postWidget(widgetDtos));
  }*/
}
