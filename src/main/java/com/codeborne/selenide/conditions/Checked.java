package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public class Checked extends WebElementCondition {

  public Checked() {
    super("checked");
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    boolean checked = element.isSelected();
    return new CheckResult(checked, checked ? "checked" : "unchecked");
  }
}
