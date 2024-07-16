package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DomProperty extends WebElementCondition {
  private final String domPropertyName;

  public DomProperty(String domPropertyName) {
    super("dom property " + domPropertyName);
    this.domPropertyName = domPropertyName;
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String domPropertyValue = element.getDomProperty(domPropertyName);
    return new CheckResult(
      domPropertyValue != null,
      String.format("%s=\"%s\"", domPropertyName, domPropertyValue)
    );
  }
}
