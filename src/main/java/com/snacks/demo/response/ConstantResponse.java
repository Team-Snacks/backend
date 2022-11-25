package com.snacks.demo.response;

public record ConstantResponse() {

  public static final String EMAIL_EXSIST = "이미 존재하는 이메일입니다.";
  public static final String EMAIL_NULL = "이메일은 필수 입력 값입니다.";
  public static final String EMAIL_NOT_FOUND = "이메일을 찾을 수 없습니다.";
  public static final String EMAIL_FORMAT_ERROR = "올바른 이메일 형식이 아닙니다.";
  public static final String PASSWORD_NULL = "비밀번호 값은 필수 입력 값입니다.";
  public static final String PASSWORD_NOT_MATCH = "비밀번호가 일치하지 않습니다.";

}
