package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.commands.Util.firstOf;

public class GetCssValue implements Command<String> {
  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    String cssPropertyName = firstOf(args);
    return locator.getWebElement().getCssValue(cssPropertyName);
  }
}
