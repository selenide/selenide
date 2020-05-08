package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Checked extends Condition {

  public Checked() {
    super("checked");
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
