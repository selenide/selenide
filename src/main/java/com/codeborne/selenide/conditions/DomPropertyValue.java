package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.Objects;

public class DomPropertyValue extends WebElementCondition {
  private final String domPropertyName;

  @Nullable
  protected final String expectedDomPropertyValue;

  public DomPropertyValue(String domPropertyName, @Nullable String expectedDomPropertyValue) {
    super(String.format("dom property %s=\"%s\"", domPropertyName, expectedDomPropertyValue));
    this.domPropertyName = domPropertyName;
    this.expectedDomPropertyValue = expectedDomPropertyValue;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String domPropertyValue = element.getDomProperty(domPropertyName);
    return new CheckResult(
      Objects.equals(expectedDomPropertyValue, domPropertyValue),
      String.format("%s=\"%s\"", domPropertyName, domPropertyValue)
    );
  }
}
