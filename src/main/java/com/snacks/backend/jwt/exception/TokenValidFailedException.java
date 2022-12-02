package com.snacks.backend.jwt.exception;

public class TokenValidFailedException extends RuntimeException{
  public TokenValidFailedException() {
    super("Failed to generate Toekn");
  }

  public TokenValidFailedException(String message) {
    super(message);
  }

}
