package com.snacks.backend.controller;

import com.snacks.backend.dto.PostUserWidgetDto;
import com.snacks.backend.dto.UserWidgetDto;
import com.snacks.backend.dto.WeatherRequestDto;
import com.snacks.backend.dto.WeatherResponseDto;
import com.snacks.backend.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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


  /**
   * 유저의 위젯 위젯 목록 반환
   * @param request
   * @param response
   * @return 유저의 위젯 목록
   */
  @GetMapping("/widgets")
  public UserWidgetDto[] getUserWidget(HttpServletRequest request, HttpServletResponse response) {
    return (userService.getUserWidget(request, response));
  }

  /**
   * 유저의 위젯 업데이트
   * @param userWidgetDtos 변경할 위젯 정보
   * @param request
   * @param response
   * @return 상태 코드 및 메시지
   */
  @PutMapping("/widgets")
  public ResponseEntity putUserWidget(@RequestBody UserWidgetDto[] userWidgetDtos, HttpServletRequest request, HttpServletResponse response) {
    return (userService.putUserWidget(userWidgetDtos, request, response));
  }

  /**
   * 유저의 위젯 삭제
   * @param duuid 삭제할 유저 위젯의 id
   * @param request
   * @param response
   * @return 상태 코드 및 메시지
   */
  @DeleteMapping("/widgets/{duuid}")
  public ResponseEntity deleteUserWidget(@PathVariable String duuid, HttpServletRequest request, HttpServletResponse response) {
    return (userService.deleteUserWidget(duuid, request, response));
  }

  /**
   * 유저의 위젯 추가
   * @param postUserWidgetDto 추가할 위젯의 정보
   * @param request
   * @param response
   * @return 현재 유저의 위젯 목록
   */
  @PostMapping("/widgets")
  public UserWidgetDto[] postUserWidget(@RequestBody PostUserWidgetDto postUserWidgetDto, HttpServletRequest request, HttpServletResponse response) {
    return (userService.postUserWidget(postUserWidgetDto, request, response));
  }

}
