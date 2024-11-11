package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.impl.JSElementFinder.wrap;

public class GetSelectedOption implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource selectElement, Object @Nullable [] args) {
    return wrap(SelenideElement.class, selectElement.driver(), selectElement.description() + " :selected", selectElement,
      "return arguments[0].options[arguments[0].selectedIndex];");
  }
}
