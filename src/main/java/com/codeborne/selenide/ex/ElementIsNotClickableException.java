package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ElementIsNotClickableException extends UIAssertionError {
  public ElementIsNotClickableException(Throwable cause) {
    super("Element is not clickable", cause);
  }
}
