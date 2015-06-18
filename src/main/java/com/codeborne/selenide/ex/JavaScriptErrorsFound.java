package com.codeborne.selenide.ex;

import java.util.List;

public class JavaScriptErrorsFound extends UIAssertionError {
  public JavaScriptErrorsFound(List<String> jsErrors) {
    super("JavaScript errors found");
    super.jsErrors = jsErrors;
  }
}
