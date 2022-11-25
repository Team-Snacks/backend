package com.snacks.demo.Auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacks.demo.ConstantTest;
import com.snacks.demo.dto.UserDto;
import com.snacks.demo.service.AuthService;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

  @Autowired
  AuthService authService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  public static void init() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  public static void close() {
    factory.close();
  }

  // 테스트 케이스
  static Stream<Arguments> loginProvider() throws Throwable {
    return Stream.of(
        Arguments.arguments(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD, "정상 케이스", true),
        Arguments.arguments("", "1234", "empty 이메일", false),
        Arguments.arguments(null, "1234", "null 이메일", false),
        Arguments.arguments("test@test.com", "", "empty 비밀번호", false),
        Arguments.arguments("test@test.com", null, "null 비밀번호", false),
        Arguments.arguments("", "", "empty 이메일, empty 비밀번호", false),
        Arguments.arguments(null, null, "null 이메일, null 비밀번호", false),
        Arguments.arguments("test", "1234", "잘못된 이메일 형식", false),
        Arguments.arguments("test", "", "잘못된 이메일 형식, null 비밀번호", false),
        Arguments.arguments("test", null, "잘못된 이메일 형식, empty 비밀번호", false)
    );
  }

  static Stream<Arguments> loginServiceProvider() throws Throwable {
    return Stream.of(
        Arguments.arguments(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD, "로그인 성공", true),
        Arguments.arguments(ConstantTest.VALID_EMAIL, "0000", "비밀번호 다름", false),
        Arguments.arguments("fake@test.com", "1234", "없는 이메일", false)
    );
  }

  @ParameterizedTest(name = "{index} - {3} - {2}")
  @DisplayName("login validation 테스트")
  @MethodSource("loginProvider")
  void signUpValidation(String email, String password, String message, boolean expected) {
    //given
    UserDto userDto = new UserDto(email, password);

    //when
    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    //then
    for (ConstraintViolation<UserDto> violation : violations) {
      System.out.println(violation.getMessage());
    }
    if (expected == true) {
      assertThat(violations.size()).isEqualTo(0);
    } else {
      assertThat(violations.size()).isNotEqualTo(0);
    }
  }

  // service 테스트
  @ParameterizedTest(name = "{index} - {3} - {2}")
  @DisplayName("login Service 테스트")
  @MethodSource("loginServiceProvider")
  void loginService(String email, String password, String message, boolean expected) {
    //given
    UserDto userDto = new UserDto(email, password);

    //when
    ResponseEntity responseEntity = authService.login(userDto);

    //then
    if (expected == true) {
      assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    } else {
      assertThat(responseEntity.getStatusCode()).isNotEqualTo(HttpStatus.OK);
    }
  }

  // controller 테스트
  @DisplayName("POST /auth 테스트")
  @Test
  void loginController() throws Exception {
    //given
    authService.signUp(new UserDto(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD));
    String user = objectMapper.writeValueAsString(
        new UserDto(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD));

    //when

    //then
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/login")
        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(user);

    mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andReturn()
        .getResponse();
  }

}
