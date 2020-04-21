package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

public class Visible extends Condition {
  public Visible() {
    super("visible");
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return element.isDisplayed();
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return String.format("visible:%s", element.isDisplayed());
  }

  @Override
  public Condition negate() {
    return new Not(this, true);
  }
}
