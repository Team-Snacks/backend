package com.snacks.backend.controller;

import com.snacks.backend.dto.PostUserWidgetDto;
import com.snacks.backend.dto.UserDto;
import com.snacks.backend.dto.UserWidgetDto;
import com.snacks.backend.dto.WidgetDto;
import com.snacks.backend.response.CommonResponse;
import com.snacks.backend.response.ListResponse;
import com.snacks.backend.response.ResponseService;
import com.snacks.backend.service.AuthService;
import com.snacks.backend.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  UserService userService;


  @GetMapping("/widgets")
  public UserWidgetDto[] getUserWidget(HttpServletRequest request, HttpServletResponse response) {
    return (userService.getUserWidget(request, response));
  }

  @PutMapping("/widgets")
  public ResponseEntity putUserWidget(@RequestBody UserWidgetDto[] userWidgetDtos, HttpServletRequest request, HttpServletResponse response) {
    return (userService.putUserWidget(userWidgetDtos, request, response));
  }

  @DeleteMapping("/widgets/{duuid}")
  public ResponseEntity deleteUserWidget(@PathVariable String duuid, HttpServletRequest request, HttpServletResponse response) {
    return (userService.deleteUserWidget(duuid, request, response));
  }

  @PostMapping("/widgets")
  public UserWidgetDto[] postUserWidget(@RequestBody PostUserWidgetDto postUserWidgetDto, HttpServletRequest request, HttpServletResponse response) {
    return (userService.postUserWidget(postUserWidgetDto, request, response));
  }

}
