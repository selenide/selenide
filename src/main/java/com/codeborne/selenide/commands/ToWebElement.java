package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

public class ToWebElement implements Command<WebElement> {
  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    WebElement element = locator.getWebElement();
    while (element instanceof SelenideElement se) {
      element = se.toWebElement();
    }
    return element;
  }
}
