package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import java.util.Objects;

public class TagName extends WebElementCondition {
  private final String expectedTagName;

  public TagName(String expectedTagName) {
    super("tag name");
    this.expectedTagName = expectedTagName;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String actualTagName = element.getTagName();
    boolean matches = Objects.equals(actualTagName, expectedTagName);
    return new CheckResult(matches, String.format("tag \"%s\"", actualTagName));
  }

  @Override
  public String toString() {
    return String.format("tag \"%s\"", expectedTagName);
  }
}
