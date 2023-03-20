package com.snacks.backend.dto;

import com.snacks.backend.response.ConstantResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 유저 정보 dto
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  //이메일
  @NotBlank(message = ConstantResponse.EMAIL_NULL)
  @Email(message = ConstantResponse.EMAIL_FORMAT_ERROR)
  private String email;

  //비밀번호
  @NotBlank(message = ConstantResponse.PASSWORD_NULL)
  private String password;

}
