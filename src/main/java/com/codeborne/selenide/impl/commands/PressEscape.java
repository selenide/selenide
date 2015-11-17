package com.codeborne.selenide.impl.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class PressEscape implements Command<WebElement> {
  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    locator.findAndAssertElementIsVisible().sendKeys(Keys.ESCAPE);
    return proxy;
  }
}
