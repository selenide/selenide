package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

public class GetSelectedOptionValue implements Command<String> {
  private final JavaScript js = new JavaScript("get-selected-option-value.js");

  @Override
  @Nullable
  public String execute(SelenideElement proxy, WebElementSource selectElement, Object @Nullable [] args) {
    return js.executeOrFail(selectElement.driver(), selectElement.getWebElement());
  }
}
