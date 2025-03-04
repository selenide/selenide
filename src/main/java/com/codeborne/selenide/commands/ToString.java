package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

public class ToString implements Command<String> {
  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    return locator.toString();
  }
}
