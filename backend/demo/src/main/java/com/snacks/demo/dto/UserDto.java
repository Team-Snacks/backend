package com.snacks.demo.dto;

import com.snacks.demo.response.ConstantResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  @NotBlank(message = ConstantResponse.EMAIL_NULL)
  @Email(message = ConstantResponse.EMAIL_FORMAT_ERROR)
  private String email;

  @NotBlank(message = ConstantResponse.PASSWORD_NULL)
  private String password;
}
