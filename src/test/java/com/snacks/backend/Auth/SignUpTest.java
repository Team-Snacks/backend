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

  // ????????? ?????????
  static Stream<Arguments> signUpProvider() throws Throwable {
    return Stream.of(
        Arguments.arguments(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD, "?????? ?????????", true),
        Arguments.arguments("", "1234", "empty ?????????", false),
        Arguments.arguments(null, "1234", "null ?????????", false),
        Arguments.arguments("test@test.com", "", "empty ????????????", false),
        Arguments.arguments("test@test.com", null, "null ????????????", false),
        Arguments.arguments("", "", "empty ?????????, empty ????????????", false),
        Arguments.arguments(null, null, "null ?????????, null ????????????", false),
        Arguments.arguments("test", "1234", "????????? ????????? ??????", false),
        Arguments.arguments("test", "", "????????? ????????? ??????, null ????????????", false),
        Arguments.arguments("test", null, "????????? ????????? ??????, empty ????????????", false)
    );
  }

  static Stream<Arguments> signUpServiceProvider() throws Throwable {
    return Stream.of(
        Arguments.arguments(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD, "???????????? ??????", true),
        Arguments.arguments(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD, "????????? ??????, ???????????? ??????", false)
    );
  }

  // validation ?????????
  @ParameterizedTest(name = "{index} - {3} - {2}")
  @DisplayName("signUp validation ?????????")
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


  // service ?????????
  @ParameterizedTest(name = "{index} - {3} - {2}")
  @DisplayName("signUp Service ?????????")
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


  // controller ?????????
  @DisplayName("POST /auth ?????????")
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
