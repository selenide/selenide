package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.FailFastCondition;

public class FailFastException extends UIAssertionError {
  public FailFastException(Driver driver, FailFastCondition condition) {
    super(driver, condition.toString());
  }
}
