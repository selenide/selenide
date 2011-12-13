package com.codeborne.security;

public class AuthenticationException extends Exception {
  public AuthenticationException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public AuthenticationException(String s) {
    super(s);
  }
}
