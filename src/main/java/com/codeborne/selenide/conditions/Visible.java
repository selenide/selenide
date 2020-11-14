package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.lang.Boolean.TRUE;

@ParametersAreNonnullByDefault
public class Visible extends Condition {
  public Visible() {
    super("visible");
  }

  @Override
  public Boolean getActualValue(Driver driver, WebElement element) {
    return element.isDisplayed();
  }

  @Override
  @CheckReturnValue
  public boolean apply(Driver driver, WebElement element, @Nullable Object actualVisibility) {
    return actualVisibility == TRUE;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String formatActualValue(@Nullable Object actualVisibility) {
    return String.format("visible:%s", actualVisibility);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Condition negate() {
    return new Not(this, true);
  }
}
