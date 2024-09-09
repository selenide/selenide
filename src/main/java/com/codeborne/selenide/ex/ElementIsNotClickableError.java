package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ElementIsNotClickableError extends UIAssertionError {
  public ElementIsNotClickableError(String elementDescription, Throwable cause) {
    super("Element is not clickable: " + elementDescription, cause);
  }
}
