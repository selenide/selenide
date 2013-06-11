package com.codeborne.selenide;

public class ElementNotFound extends AssertionError {
  public ElementNotFound(String searchCriteria, Condition expectedCondition, long timeoutMs) {
    super(searchCriteria + "\nExpected: " + expectedCondition + "\nTimeout: " + timeoutMs/1000 + " s.");
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getMessage();
  }
}
