package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

public class ContextClick implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    locator.driver().actions().contextClick(locator.findAndAssertElementIsVisibleOrOpaque()).perform();
    return proxy;
  }
}
