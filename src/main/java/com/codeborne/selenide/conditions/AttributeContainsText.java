package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AttributeContainsText extends Condition {

  private final String attributeName;
  private final String expectedAttributeValue;

  public AttributeContainsText(String attributeName, String expectedAttributeValue) {
    super("attribute contains text");
    this.attributeName = attributeName;
    this.expectedAttributeValue = expectedAttributeValue;
  }

  @CheckReturnValue
  @Override
  public boolean apply(Driver driver, WebElement element) {
    return getAttributeValue(element).contains(expectedAttributeValue);
  }

  protected String getAttributeValue(WebElement element) {
    String attr = element.getAttribute(attributeName);
    return attr == null ? "" : attr;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s %s=\"%s\"", getName(), attributeName, expectedAttributeValue);
  }
}
