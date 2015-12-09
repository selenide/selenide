package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

public class GetValue implements Command<String> {
  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    return locator.getWebElement().getAttribute("value");
  }
}
