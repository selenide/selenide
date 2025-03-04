package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public class Selected extends WebElementCondition {

  public Selected() {
    super("selected");
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    boolean selected = element.isSelected();
    return new CheckResult(selected, selected ? "selected" : "not selected");
  }
}
