package com.snacks.backend.service;

import com.snacks.backend.dto.PosDto;
import com.snacks.backend.dto.PostUserWidgetDto;
import com.snacks.backend.dto.SizeDto;
import com.snacks.backend.dto.UserWidgetDto;
import com.snacks.backend.entity.User;
import com.snacks.backend.entity.UserWidget;
import com.snacks.backend.entity.Widget;
import com.snacks.backend.repository.AuthRepository;
import com.snacks.backend.repository.UserWidgetRepository;
import com.snacks.backend.repository.WidgetRepository;
import com.snacks.backend.response.ConstantResponse;
import com.snacks.backend.response.ResponseService;
import java.util.Optional;
import java.util.Vector;
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


  public UserService(AuthRepository authRepository, ResponseService responseService, WidgetRepository widgetRepository, UserWidgetRepository userWidgetRepository) {
    this.authRepository = authRepository;
    this.responseService = responseService;
    this.widgetRepository = widgetRepository;
    this.userWidgetRepository = userWidgetRepository;
  }
  public UserWidgetDto[] getUserWidget(String email){
    Optional<User> user = authRepository.findByEmail(email);

    /*if (!user.isPresent()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(responseService.errorResponse(ConstantResponse.EMAIL_NOT_FOUND));
    }*/
    UserWidget[] userWidgets = userWidgetRepository.findWidgets(user.get().getId());

    //if (userWidgets == null) null이면??? 어떡하지 그냥 빈칸 아닌가

    Vector<UserWidgetDto> vector = new Vector<>();

    for(UserWidget userWidget : userWidgets) {
      UserWidgetDto userWidgetDto = new UserWidgetDto();
      Widget widget = widgetRepository.findByWidgetId(userWidget.getWidgetId());

      /*if (widget == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(responseService.errorResponse("존재하지 않는 위젯입니다.")); //log 저기다 정리하기 하는김에 다른 파일도
      }*/

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

    //return ResponseEntity.status(HttpStatus.OK)
    //    .body(responseService.getListResponse(widgetDtos));
    return widgetDtos;



  }

  @Transactional
  public ResponseEntity putUserWidget(String email, UserWidgetDto[] userWidgetDtos) {
    Optional<User> user = authRepository.findByEmail(email);

    for (UserWidgetDto userWidgetDto : userWidgetDtos) {

      UserWidget userWidget = userWidgetRepository.findById(userWidgetDto.getDuuid());

      userWidget.update(userWidgetDto.getPos().getX(), userWidgetDto.getPos().getY(), userWidgetDto.getSize().getW(), userWidgetDto.getSize().getH(),
          userWidgetDto.getData());
    }

    return ResponseEntity.status(HttpStatus.CREATED).
        body(responseService.getCommonResponse());
  }

  public ResponseEntity deleteUserWidget(String email, String duuid) {

    Long id = Long.parseLong(duuid);
    UserWidget userWidget = userWidgetRepository.findById(id);
    userWidgetRepository.delete(userWidget);

    return ResponseEntity.status(HttpStatus.OK).
        body(responseService.getCommonResponse());
  }

  public UserWidgetDto[] postUserWidget(String email, PostUserWidgetDto postUserWidgetDto){
    Optional<User> user = authRepository.findByEmail(email);
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

    return getUserWidget(email);
  }
}

