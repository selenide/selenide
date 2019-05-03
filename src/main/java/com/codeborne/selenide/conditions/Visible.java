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
}
