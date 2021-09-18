package com.codeborne.selenide.ex;

public class PageObjectException extends RuntimeException {
  public PageObjectException(String message, ReflectiveOperationException cause) {
    super(message, cause);
  }
}
