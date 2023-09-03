package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Disabled extends WebElementCondition {

  public Disabled() {
    super("disabled");
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    boolean disabled = !element.isEnabled();
    return new CheckResult(disabled, disabled ? "disabled" : "enabled");
  }
}
