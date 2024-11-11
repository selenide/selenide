package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public class DomAttribute extends WebElementCondition {
  private final String domAttributeName;

  public DomAttribute(String domAttributeName) {
    super("dom attribute " + domAttributeName);
    this.domAttributeName = domAttributeName;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String attributeValue = element.getDomAttribute(domAttributeName);
    return new CheckResult(
      attributeValue != null,
      String.format("%s=\"%s\"", domAttributeName, attributeValue)
    );
  }
}
