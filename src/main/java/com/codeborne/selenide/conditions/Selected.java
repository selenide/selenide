package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

public class Selected extends Condition {

  public Selected() {
    super("selected");
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return element.isSelected();
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return String.valueOf(element.isSelected());
  }
}
