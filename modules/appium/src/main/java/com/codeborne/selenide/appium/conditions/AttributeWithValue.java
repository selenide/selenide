package com.codeborne.selenide.appium.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AttributeWithValue extends Condition {
  private final CombinedAttribute attribute;
  protected final String expectedAttributeValue;

  public AttributeWithValue(CombinedAttribute attribute, String expectedAttributeValue) {
    super(String.format("attribute %s=\"%s\"", attribute, expectedAttributeValue));
    this.attribute = attribute;
    this.expectedAttributeValue = expectedAttributeValue;
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String attributeValue = attribute.getAttributeValue(driver, element);
    return new CheckResult(
      expectedAttributeValue.equals(attributeValue),
      String.format("%s=\"%s\"", attribute, attributeValue)
    );
  }
}
