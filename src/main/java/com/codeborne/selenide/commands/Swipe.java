package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.interactions.Actions;

abstract class Swipe implements Command<SelenideElement> {

  protected abstract int getSwipingXDistance(SelenideElement proxy, WebElementSource locator);

  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    new Actions(locator.driver().getWebDriver())
      .dragAndDropBy(locator.getWebElement(), getSwipingXDistance(proxy, locator), 0).perform();
    return proxy;
  }
}
