package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public class Enabled extends WebElementCondition {

  public Enabled() {
    super("enabled");
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    boolean enabled = element.isEnabled();
    return new CheckResult(enabled, enabled ? "enabled" : "disabled");
  }
}
