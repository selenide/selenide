package com.codeborne.selenide.ex;

import java.util.List;

public class JavaScriptErrorsFound extends UIAssertionError {
  private final List<String> jsErrors;

  public JavaScriptErrorsFound(List<String> jsErrors) {
    super("JavaScript errors found");
    this.jsErrors = jsErrors;
  }

  public List<String> getJsErrors() {
    return jsErrors;
  }
}
