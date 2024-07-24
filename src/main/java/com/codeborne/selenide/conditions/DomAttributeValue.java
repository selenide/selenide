package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class DomAttributeValue extends WebElementCondition {
  private final String domAttributeName;

  @Nullable
  private final String expectedDomAttributeValue;

  public DomAttributeValue(String domAttributeName, @Nullable String expectedDomAttributeValue) {
    super(String.format("dom attribute %s=\"%s\"", domAttributeName, expectedDomAttributeValue));
    this.domAttributeName = domAttributeName;
    this.expectedDomAttributeValue = expectedDomAttributeValue;
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String attributeValue = element.getDomAttribute(domAttributeName);
    return new CheckResult(
      Objects.equals(expectedDomAttributeValue, attributeValue),
      String.format("%s=\"%s\"", domAttributeName, attributeValue)
    );
  }
}
