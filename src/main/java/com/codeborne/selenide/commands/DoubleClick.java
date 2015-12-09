package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import static com.codeborne.selenide.Selenide.actions;

public class DoubleClick implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    actions().doubleClick(locator.findAndAssertElementIsVisible()).perform();
    return proxy;
  }
}
