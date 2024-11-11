package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

public class Title implements ObjectCondition<WebDriver> {

  private final String expectedTitle;

  public Title(String expectedTitle) {
    this.expectedTitle = expectedTitle;
  }

  @Override
  public String expectedValue() {
    return expectedTitle;
  }

  @Override
  public String description() {
    return "should have title " + expectedTitle;
  }

  @Override
  public String negativeDescription() {
    return "should not have title " + expectedTitle;
  }

  @Override
  public String describe(WebDriver webDriver) {
    return "Page";
  }

  @Override
  public CheckResult check(WebDriver webDriver) {
    String title = webDriver.getTitle();
    return result(webDriver, Objects.equals(title, expectedTitle), title);
  }

}
