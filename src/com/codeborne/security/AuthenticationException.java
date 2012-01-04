package com.codeborne.security;

public class AuthenticationException extends Exception {
  private String details;
  
  public AuthenticationException(String code, String details, Throwable throwable) {
    super(code, throwable);
    this.details = details;
  }

  public AuthenticationException(String code) {
    super(code);
  }

  @Override
  public String toString() {
    return getClass().getName() + ": " + getMessage() + (details != null ? ": " + details : "");
  }
}
