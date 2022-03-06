package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Attribute extends Condition {
  private final String attributeName;

  public Attribute(String attributeName) {
    super("attribute " + attributeName);
    this.attributeName = attributeName;
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String attributeValue = element.getAttribute(attributeName);
    return new CheckResult(attributeValue != null, String.format("%s=\"%s\"", attributeName, attributeValue));
  }
}
