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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
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
    String serviceKey = env.getProperty("SERVICE_KEY");
    LocalDateTime now = LocalDateTime.now();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String base_day = now.format(formatter);

    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
    String base_time = now.format(timeFormatter);


    StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst");
    try {
      urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
      urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
      urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
      urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
      urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(base_day, "UTF-8")); /*날짜*/
      urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(base_time, "UTF-8")); /*시간*/
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
    String result = sb.toString();

    System.out.println(result);


      JSONObject parse_response = new JSONObject(result);
      String response = parse_response.optString("response");

      JSONObject parse_header = new JSONObject(response);
      String header = parse_header.optString("header");

      JSONObject parse_code = new JSONObject(header);
      String code = parse_code.optString("resultCode");

      if (code.equals("00")) {

        JSONObject parse_body = new JSONObject(response);
        String body = parse_body.optString("body");

        JSONObject parse_itmes = new JSONObject(body);
        String items = parse_itmes.optString("items");

        JSONObject parse_item = new JSONObject(items);
        JSONArray jsonArray = parse_item.optJSONArray("item");

        Vector<WeatherResponseDto> vector = new Vector<>();
        for (int i = 0; i < jsonArray.length(); i++) {
          parse_item = jsonArray.getJSONObject(i);
          String fcstValue = parse_item.optString("fcstValue");
          String category = parse_item.optString("category");

          if (category.equals("POP") || category.equals("PTY") || category.equals("SKY") ||
              category.equals("TMP") || category.equals("TMN") || category.equals("TMX"))
          {
            WeatherResponseDto tmp = new WeatherResponseDto();
            tmp.setFcstValue(Double.parseDouble(fcstValue));
            tmp.setCategory(category);
            vector.add(tmp);

            System.out.print(i + "번쨰 :  ");
            System.out.print("fcstValue : " + fcstValue);
            System.out.print("  category : " + category);
            System.out.println();

          }
        }

        WeatherResponseDto[] weatherResponseDtos = new WeatherResponseDto[vector.size()];
        for (int i = 0; i < vector.size(); i++)
          weatherResponseDtos[i] = vector.get(i);

        //return weatherResponseDtos; 반환값 나중에 추가
      }
    } catch (MalformedURLException urlException) {
      System.out.println(urlException);
    } catch (IOException ioException) {
      System.out.println(ioException);
    }

  }
}

