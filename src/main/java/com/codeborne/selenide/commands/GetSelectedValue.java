package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GetSelectedValue implements Command<String> {
  @Override
  @CheckReturnValue
  @Nullable
  public String execute(SelenideElement proxy, WebElementSource selectElement, @Nullable Object[] args) {
    return selectElement.driver().executeJavaScript(
      "return arguments[0].options[arguments[0].selectedIndex]?.value",
      selectElement.getWebElement()
    );
  }
}
