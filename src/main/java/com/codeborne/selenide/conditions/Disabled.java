package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public class Disabled extends WebElementCondition {

  public Disabled() {
    super("disabled");
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    boolean disabled = !element.isEnabled();
    return new CheckResult(disabled, disabled ? "disabled" : "enabled");
  }
}
