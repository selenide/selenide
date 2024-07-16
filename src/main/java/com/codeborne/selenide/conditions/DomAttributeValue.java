package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DomAttributeValue extends WebElementCondition {
  private final String domAttributeName;
  protected final String expectedDomAttributeValue;

  public DomAttributeValue(String domAttributeName, String expectedDomAttributeValue) {
    super(String.format("dom attribute %s=\"%s\"", domAttributeName, expectedDomAttributeValue));
    this.domAttributeName = domAttributeName;
    this.expectedDomAttributeValue = expectedDomAttributeValue;
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String attributeValue = element.getDomAttribute(domAttributeName);
    return new CheckResult(
      expectedDomAttributeValue.equals(attributeValue),
      String.format("%s=\"%s\"", domAttributeName, attributeValue)
    );
  }
}
