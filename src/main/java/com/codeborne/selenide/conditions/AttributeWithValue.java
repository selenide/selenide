package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

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
    return expectedAttributeValue.equals(getAttributeValue(element, attributeName));
  }

  @Override
  public String toString() {
    return getName() + " " + attributeName + '=' + expectedAttributeValue;
  }

  private String getAttributeValue(WebElement element, String attributeName) {
    String attr = element.getAttribute(attributeName);
    return attr == null ? "" : attr;
  }
}
