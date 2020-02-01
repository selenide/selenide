package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

public class GetPseudoValue implements Command<String> {

  static final String JS_CODE = "return window.getComputedStyle(arguments[0], arguments[1])" +
    ".getPropertyValue(arguments[2]);";

  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    if (args.length > 1) {
      return locator.driver().executeJavaScript(JS_CODE, locator.getWebElement(), args[0], args[1]);
    }

    return locator.driver().executeJavaScript(JS_CODE, locator.getWebElement(), args[0], "content");
  }
}
