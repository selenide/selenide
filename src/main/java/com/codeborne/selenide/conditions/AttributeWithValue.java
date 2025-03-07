package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public class AttributeWithValue extends WebElementCondition {
  private final String attributeName;
  protected final String expectedAttributeValue;

  public AttributeWithValue(String attributeName, String expectedAttributeValue) {
    super(String.format("attribute %s=\"%s\"", attributeName, expectedAttributeValue));
    this.attributeName = attributeName;
    this.expectedAttributeValue = expectedAttributeValue;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String attributeValue = getAttributeValue(element);
    return new CheckResult(
      expectedAttributeValue.equals(attributeValue),
      String.format("%s=\"%s\"", attributeName, attributeValue)
    );
  }

  protected String getAttributeValue(WebElement element) {
    String attr = element.getAttribute(attributeName);
    return attr == null ? "" : attr;
  }
}
