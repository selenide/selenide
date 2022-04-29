package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;

@ParametersAreNonnullByDefault
public class PseudoElementPropertyWithValue extends Condition {

  static final String JS_CODE = "return window.getComputedStyle(arguments[0], arguments[1])" +
    ".getPropertyValue(arguments[2]);";

  private final String pseudoElementName;
  private final String propertyName;
  private final String expectedPropertyValue;

  public PseudoElementPropertyWithValue(String pseudoElementName, String propertyName, String expectedPropertyValue) {
    super("pseudo-element");
    this.pseudoElementName = pseudoElementName;
    this.propertyName = propertyName;
    this.expectedPropertyValue = expectedPropertyValue;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String value = getPseudoElementPropertyValue(driver, element);
    return new CheckResult(defaultString(expectedPropertyValue).equalsIgnoreCase(value), actualValue(value));
  }

  private String actualValue(String pseudoElementValue) {
    return String.format("%s {%s: %s;}", pseudoElementName, propertyName, pseudoElementValue);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s %s {%s: %s;}", getName(), pseudoElementName, propertyName, expectedPropertyValue);
  }

  private String getPseudoElementPropertyValue(Driver driver, WebElement element) {
    String propertyValue = driver.executeJavaScript(JS_CODE, element, pseudoElementName, propertyName);
    return propertyValue == null ? EMPTY : propertyValue;
  }
}
