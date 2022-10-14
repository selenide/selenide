package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GetSelectedOptionValue implements Command<String> {
  private final JavaScript js = new JavaScript("get-selected-option-value.js");

  @Override
  @CheckReturnValue
  @Nullable
  public String execute(SelenideElement proxy, WebElementSource selectElement, @Nullable Object[] args) {
    return js.executeOrFail(selectElement.driver(), selectElement.getWebElement());
  }
}
