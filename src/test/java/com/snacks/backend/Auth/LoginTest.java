package com.snacks.backend.Auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacks.backend.ConstantTest;
import com.snacks.backend.dto.UserDto;
import com.snacks.backend.service.AuthService;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

  // controller 테스트
  @DisplayName("토큰 발급 테스트")
  @Test
  void create_token_test() throws Exception {
    //given
    authService.signUp(new UserDto(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD));
    String user = objectMapper.writeValueAsString(
        new UserDto(ConstantTest.VALID_EMAIL, ConstantTest.VALID_PASSWORD));

    //when

    //then
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(user);

    mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andReturn()
        .getResponse();

  }

}
