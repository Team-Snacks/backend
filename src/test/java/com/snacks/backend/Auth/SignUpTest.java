package com.snacks.backend.Auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacks.backend.ConstantTest;
import com.snacks.backend.config.SecurityConfig;
import com.snacks.backend.controller.AuthController;
import com.snacks.backend.dto.UserDto;
import com.snacks.backend.jwt.JwtAuthenticationFilter;
import com.snacks.backend.jwt.JwtAuthorizationFilter;
import com.snacks.backend.redis.RedisService;
import com.snacks.backend.repository.AuthRepository;
import com.snacks.backend.service.AuthService;
import java.util.Set;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.server.standard.SpringConfigurator;

@SpringBootTest
@AutoConfigureMockMvc
public class SignUpTest {

  private static ValidatorFactory factory;
  private static Validator validator;


  @Autowired
  private RedisService redisService;

  @Autowired
  private AuthRepository authRepository;

  @Autowired
  private WebApplicationContext context;


  private MockMvc mockMvc;

  @Autowired
  private SecurityConfig securityConfig;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AuthService authService;


  @BeforeAll
  public static void init() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }
  @BeforeEach
  public void getUpMockMvc(){
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .alwaysDo(print())
        .build();
  }

  @AfterAll
  public static void close() {
    factory.close();
  }

  // 테스트 케이스
  static Stream<Arguments> signUpProvider() throws Throwable {
    return Stream.of(
        Arguments.arguments(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD, "정상 테스트", true),
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

  static Stream<Arguments> signUpServiceProvider() throws Throwable {
    return Stream.of(
        Arguments.arguments(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD, "회원가입 성공", true),
        Arguments.arguments(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD, "이메일 중복, 회원가입 실패", false)
    );
  }

  // validation 테스트
  @ParameterizedTest(name = "{index} - {3} - {2}")
  @DisplayName("signUp validation 테스트")
  @MethodSource("signUpProvider")
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
  @DisplayName("signUp Service 테스트")
  @MethodSource("signUpServiceProvider")
  void signUpService(String email, String password, String message, boolean expected) {
    //given
    UserDto userDto = new UserDto(email, password);

    //when
    ResponseEntity responseEntity = authService.signUp(userDto);
    //then
    if (expected == true) {
      assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    } else {
      assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
  }


  // controller 테스트
  @DisplayName("POST /auth 테스트")
  @Test
  void signupController() throws Exception {
    //given
    String user = objectMapper.writeValueAsString(new UserDto("signup@email.com", "1234"));

    //when

    //then
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth")
        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(user);

    mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andDo(print())
        .andReturn()
        .getResponse();
  }

}
