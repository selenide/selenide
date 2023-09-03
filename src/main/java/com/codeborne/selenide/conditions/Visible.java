package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Visible extends WebElementCondition {
  public Visible() {
    super("visible");
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    boolean displayed = element.isDisplayed();
    return new CheckResult(displayed, displayed ? "visible" : "hidden");
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElementCondition negate() {
    return new Not(this, true);
  }
}
