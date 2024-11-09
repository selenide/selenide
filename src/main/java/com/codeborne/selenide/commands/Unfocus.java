package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

public class Unfocus extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    locator.driver().executeJavaScript("arguments[0].blur()", locator.getWebElement());
  }
}
