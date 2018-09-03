package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Configuration.setValueChangeEvent;
import static com.codeborne.selenide.impl.Events.events;

public class Append implements Command<WebElement> {
  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    WebElement input = locator.getWebElement();
    input.sendKeys((String) args[0]);
    if (setValueChangeEvent) {
      events.fireChangeEvent(locator.context(), input);
    }
    return proxy;
  }
}
