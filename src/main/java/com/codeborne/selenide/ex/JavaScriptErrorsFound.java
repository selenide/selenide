package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import java.util.List;

public class JavaScriptErrorsFound extends UIAssertionError {
  public JavaScriptErrorsFound(Driver driver, List<String> jsErrors) {
    super(driver, "JavaScript errors found");
    super.jsErrors = jsErrors;
  }
}
