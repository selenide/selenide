package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import static com.codeborne.selenide.commands.Util.firstOf;
import static java.util.Objects.requireNonNull;

public class GetPseudoValue implements Command<String> {

  static final String JS_CODE = """
    return window.getComputedStyle(arguments[0], arguments[1]).getPropertyValue(arguments[2]);
    """;

  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String pseudoElement = firstOf(args);
    String propertyName = args.length > 1 ?
      (String) requireNonNull(args[1], "property name is a required parameter") :
      "content";
    return requireNonNull(locator.driver().executeJavaScript(JS_CODE, locator.getWebElement(), pseudoElement, propertyName));
  }
}
