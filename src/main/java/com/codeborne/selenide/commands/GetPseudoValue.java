package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class GetPseudoValue implements Command<String> {

  static final String JS_CODE = "return window.getComputedStyle(arguments[0], arguments[1])" +
    ".getPropertyValue(arguments[2]);";

  @Override
  @CheckReturnValue
  @Nonnull
  public String execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    String pseudoElement = firstOf(args);

    if (args.length > 1) {
      String propertyName = (String) args[1];
      return locator.driver().executeJavaScript(JS_CODE, locator.getWebElement(), pseudoElement, propertyName);
    }

    return locator.driver().executeJavaScript(JS_CODE, locator.getWebElement(), pseudoElement, "content");
  }
}
