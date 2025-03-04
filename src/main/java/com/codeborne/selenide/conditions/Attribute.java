package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public class Attribute extends WebElementCondition {
  private final String attributeName;

  public Attribute(String attributeName) {
    super("attribute " + attributeName);
    this.attributeName = attributeName;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String attributeValue = element.getAttribute(attributeName);
    return new CheckResult(attributeValue != null, String.format("%s=\"%s\"", attributeName, attributeValue));
  }
}
