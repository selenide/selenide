package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class Hidden extends Condition {
  public Hidden() {
    super("hidden", true);
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    try {
      return !element.isDisplayed();
    }
    catch (StaleElementReferenceException elementHasDisappeared) {
      return true;
    }
  }

  @Override
  public Condition negate() {
    return new Not(this, false);
  }
}
