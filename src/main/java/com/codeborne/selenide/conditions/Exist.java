package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Exist extends Condition {
  public Exist() {
    super("exist");
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    try {
      element.isDisplayed();
      return true;
    }
    catch (StaleElementReferenceException e) {
      return false;
    }
  }

  @Nonnull
  @Override
  public Condition negate() {
    return new Not(this, true);
  }
}
