package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

public class GetMaxLength implements Command<String> {

  @Override
  public String execute(final SelenideElement proxy, final WebElementSource locator, final Object[] args) {
    return locator.getWebElement().getAttribute("maxlength");
  }
}
