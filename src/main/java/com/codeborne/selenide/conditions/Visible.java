package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public class Visible extends WebElementCondition {
  public Visible() {
    super("visible");
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    boolean displayed = element.isDisplayed();
    return new CheckResult(displayed, displayed ? "visible" : "hidden");
  }

  @Override
  public WebElementCondition negate() {
    return new Not(this, true);
  }
}
