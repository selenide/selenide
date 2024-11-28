package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.commands.Util.firstOf;

public class GetAttribute implements Command<String> {
  @Override
  @Nullable
  public String execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    String attributeName = firstOf(args);
    return locator.getWebElement().getAttribute(attributeName);
  }
}
