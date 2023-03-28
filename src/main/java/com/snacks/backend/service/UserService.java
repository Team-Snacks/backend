package com.snacks.backend.service;

import com.snacks.backend.config.EnvConfiguration;
import com.snacks.backend.dto.*;
import com.snacks.backend.entity.User;
import com.snacks.backend.entity.UserWidget;
import com.snacks.backend.entity.Widget;
import com.snacks.backend.jwt.JwtProvider;
import com.snacks.backend.repository.AuthRepository;
import com.snacks.backend.repository.UserWidgetRepository;
import com.snacks.backend.repository.WidgetRepository;
import com.snacks.backend.response.ResponseService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

  public void weatherWidget(WeatherRequestDto weatherRequestDto) //throws IOException
  {
    String serviceKey = "ia5tvugbDgT2IDf9oME4OPwXUuN252wpqS8vJi%2Bk922X37kOZ25EXBAbW6ayJKT2z0teNaVglVRbDoHXLQk1kw%3D%3D";

    LocalDate now = LocalDate.now();
    LocalTime timeNow = LocalTime.now();

    /*String year = Integer.toString(now.getYear());
    String month;
    int monthValue = now.getMonthValue();
    if (monthValue >= 1 && monthValue <= 9 )
      month = "0" + monthValue;
    else
      month = Integer.toString(monthValue);
    String day = Integer.toString(now.getDayOfMonth());

    String base_day = year + month + day;*/

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String base_day = now.format(formatter);

    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
    String base_time = now.format(timeFormatter);


    StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst");
    try {
      urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
      urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
      urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
      urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
      urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(base_day, "UTF-8")); /*‘21년 6월 28일 발표*/
      urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(base_time, "UTF-8")); /*06시 발표(정시단위) */
      urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(weatherRequestDto.getX().toString(), "UTF-8")); /*예보지점의 X 좌표값*/
      urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(weatherRequestDto.getY().toString(), "UTF-8")); /*예보지점의 Y 좌표값*/
    } catch (UnsupportedEncodingException e) {
      System.out.println(e);
    }
    try {
      URL url = new URL(urlBuilder.toString());
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Content-type", "application/json");
    System.out.println("Response code: " + conn.getResponseCode());
    BufferedReader rd;
    if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
      rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    } else {
      rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
    }
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      sb.append(line);
    }
    rd.close();
    conn.disconnect();
    System.out.println(sb.toString());
    } catch (MalformedURLException urlException) {
      System.out.println(urlException);
    } catch (IOException ioException) {
      System.out.println(ioException);
    }

  }
}

