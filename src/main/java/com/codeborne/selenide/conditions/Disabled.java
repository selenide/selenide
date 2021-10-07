package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.conditions.enabled.ElementValidation;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Disabled extends Condition {

  private final ElementValidation elementValidation = new ElementValidation();

  public Disabled() {
    super("disabled");
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    elementValidation.validaElementIsApplicableForEnabledCondition(element);
    return !element.isEnabled();
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return element.isEnabled() ? "enabled" : "disabled";
  }
}
