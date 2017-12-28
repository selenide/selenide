package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class ScrollIntoView implements Command<WebElement> {
  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String param = args[0].toString();
    executeJavaScript("arguments[0].scrollIntoView(" + param + ")", proxy);
    return proxy;
  }
}
