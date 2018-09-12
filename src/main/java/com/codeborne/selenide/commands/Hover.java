package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class Hover implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    WebElement element = locator.getWebElement();
    new Actions(locator.driver().getWebDriver()).moveToElement(element).perform();
    return proxy;
  }
}
