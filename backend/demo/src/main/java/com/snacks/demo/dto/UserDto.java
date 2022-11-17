package com.snacks.demo.dto;

import com.snacks.demo.response.ResponseMessage;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
  @NotBlank(message = ResponseMessage.EMAIL_NULL)
  @Email(message = ResponseMessage.EMAIL_FORMAT_ERROR)
  private String email;

  @NotBlank(message = ResponseMessage.PASSWORD_NULL)
  private String password;
}
