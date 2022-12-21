package com.snacks.backend.service;

import com.snacks.backend.config.EnvConfiguration;
import com.snacks.backend.dto.PosDto;
import com.snacks.backend.dto.PostUserWidgetDto;
import com.snacks.backend.dto.SizeDto;
import com.snacks.backend.dto.UserWidgetDto;
import com.snacks.backend.entity.User;
import com.snacks.backend.entity.UserWidget;
import com.snacks.backend.entity.Widget;
import com.snacks.backend.jwt.JwtProvider;
import com.snacks.backend.repository.AuthRepository;
import com.snacks.backend.repository.UserWidgetRepository;
import com.snacks.backend.repository.WidgetRepository;
import com.snacks.backend.response.ConstantResponse;
import com.snacks.backend.response.ResponseService;
import java.util.Optional;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  @Autowired
  AuthRepository authRepository;

  @Autowired
  ResponseService responseService;

  @Autowired
  WidgetRepository widgetRepository;

  @Autowired
  UserWidgetRepository userWidgetRepository;

  @Autowired
  EnvConfiguration env;

  @Autowired
  JwtProvider jwtProvider;


  public UserService(AuthRepository authRepository, ResponseService responseService, WidgetRepository widgetRepository, UserWidgetRepository userWidgetRepository) {
    this.authRepository = authRepository;
    this.responseService = responseService;
    this.widgetRepository = widgetRepository;
    this.userWidgetRepository = userWidgetRepository;
  }
  public UserWidgetDto[] getUserWidget(HttpServletRequest request, HttpServletResponse response){

    String token = request.getHeader("Authorization")
        .replace("Bearer ", "");

    String provider = jwtProvider.getProvider(token);
    String email = jwtProvider.getEmail(token);


    Optional<User> user = authRepository.findByEmailAndProvider(email, provider);

    UserWidget[] userWidgets = userWidgetRepository.findWidgets(user.get().getId());

    Vector<UserWidgetDto> vector = new Vector<>();

    for(UserWidget userWidget : userWidgets) {
      UserWidgetDto userWidgetDto = new UserWidgetDto();
      Widget widget = widgetRepository.findByWidgetId(userWidget.getWidgetId());


      userWidgetDto.setDuuid(userWidget.getId());
      userWidgetDto.setName(widget.getName());
      userWidgetDto.setPos(new PosDto(userWidget.getX(), userWidget.getW()));
      userWidgetDto.setSize(new SizeDto(userWidget.getW(), userWidget.getH()));
      userWidgetDto.setData(userWidget.getData());
      vector.add(userWidgetDto);
    }

    UserWidgetDto[] widgetDtos = new UserWidgetDto[vector.size()];
    for (int i = 0; i < vector.size(); i++) {
      widgetDtos[i] = vector.get(i);
    }

    return widgetDtos;



  }

  @Transactional
  public ResponseEntity putUserWidget(UserWidgetDto[] userWidgetDtos, HttpServletRequest request, HttpServletResponse response) {


    /*String token = request.getHeader("Authorization")
        .replace("Bearer ", "");

    String provider = jwtProvider.getProvider(token);
    String email = jwtProvider.getEmail(token);

    Optional<User> user = authRepository.findByEmailAndProvider(email, provider);

    //고유 아이디인 duuid를 사용하기에 토큰을 사용해 유저를 찾을 필요가 없음
    */


    for (UserWidgetDto userWidgetDto : userWidgetDtos) {

      UserWidget userWidget = userWidgetRepository.findById(userWidgetDto.getDuuid());

      userWidget.update(userWidgetDto.getPos().getX(), userWidgetDto.getPos().getY(), userWidgetDto.getSize().getW(), userWidgetDto.getSize().getH(),
          userWidgetDto.getData());
    }

    return ResponseEntity.status(HttpStatus.CREATED).
        body(responseService.getCommonResponse());
  }

  public ResponseEntity deleteUserWidget(String duuid, HttpServletRequest request, HttpServletResponse response) {

    Long id = Long.parseLong(duuid);
    UserWidget userWidget = userWidgetRepository.findById(id);
    userWidgetRepository.delete(userWidget);

    return ResponseEntity.status(HttpStatus.OK).
        body(responseService.getCommonResponse());
  }

  public UserWidgetDto[] postUserWidget(PostUserWidgetDto postUserWidgetDto, HttpServletRequest request, HttpServletResponse response){

    String token = request.getHeader("Authorization")
        .replace("Bearer ", "");

    String provider = jwtProvider.getProvider(token);
    String email = jwtProvider.getEmail(token);

    Optional<User> user = authRepository.findByEmailAndProvider(email, provider);
    Widget widget = widgetRepository.findByName(postUserWidgetDto.getName());

    UserWidget userWidget = new UserWidget();

    userWidget.setUser(user.get());
    userWidget.setWidget(widget);
    userWidget.setUserId(user.get().getId());
    userWidget.setWidgetId(widget.getId());
    userWidget.setX(postUserWidgetDto.getPos().getX());
    userWidget.setY(postUserWidgetDto.getPos().getY());
    userWidget.setW(postUserWidgetDto.getSize().getW());
    userWidget.setH(postUserWidgetDto.getSize().getH());
    userWidget.setTitle(postUserWidgetDto.getName());
    userWidget.setData(postUserWidgetDto.getData());

    userWidgetRepository.save(userWidget);

    return getUserWidget(request, response);
  }
}

