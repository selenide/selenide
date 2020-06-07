package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class GetDataAttribute implements Command<String> {
  @Override
  @CheckReturnValue
  @Nullable
  public String execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    String dataAttributeName = firstOf(args);
    return locator.getWebElement().getAttribute("data-" + dataAttributeName);
  }
}
