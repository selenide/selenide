package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class Visible extends Condition {
  public Visible() {
    super("visible");
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    try{
      return element.isDisplayed();
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return String.format("visible:%s", element.isDisplayed());
  }
}
