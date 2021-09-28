package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Disabled extends Condition {

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
