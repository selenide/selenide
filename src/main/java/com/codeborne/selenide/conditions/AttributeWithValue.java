package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AttributeWithValue extends Condition {
  private final String attributeName;
  private final String expectedAttributeValue;

  public AttributeWithValue(String attributeName, String expectedAttributeValue) {
    super("attribute");
    this.attributeName = attributeName;
    this.expectedAttributeValue = expectedAttributeValue;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return expectedAttributeValue.equals(getAttributeValue(element));
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return String.format("%s=\"%s\"", attributeName, getAttributeValue(element));
  }

  @Override
  public String toString() {
    return String.format("%s %s=\"%s\"", getName(), attributeName, expectedAttributeValue);
  }

  private String getAttributeValue(WebElement element) {
    String attr = element.getAttribute(attributeName);
    return attr == null ? "" : attr;
  }
}
