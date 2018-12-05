package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class PressEnter implements Command<WebElement> {
  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    locator.findAndAssertElementIsInteractable().sendKeys(Keys.ENTER);
    return proxy;
  }
}
