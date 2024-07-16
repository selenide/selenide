package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DomPropertyValue extends WebElementCondition {
  private final String domPropertyName;
  protected final String expectedDomPropertyValue;

  public DomPropertyValue(String domPropertyName, String expectedDomPropertyValue) {
    super(String.format("dom property %s=\"%s\"", domPropertyName, expectedDomPropertyValue));
    this.domPropertyName = domPropertyName;
    this.expectedDomPropertyValue = expectedDomPropertyValue;
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String domPropertyValue = element.getDomProperty(domPropertyName);
    return new CheckResult(
      expectedDomPropertyValue.equals(domPropertyValue),
      String.format("%s=\"%s\"", domPropertyName, domPropertyValue)
    );
  }
}
