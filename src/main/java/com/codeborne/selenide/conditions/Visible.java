package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Visible extends Condition {
  public Visible() {
    super("visible");
  }

  @Override
  @CheckReturnValue
  public boolean apply(Driver driver, WebElement element) {
    return element.isDisplayed();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String actualValue(Driver driver, WebElement element) {
    return String.format("visible:%s", element.isDisplayed());
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Condition negate() {
    return new Not(this, true);
  }
}
