package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.impl.JSElementFinder.wrap;

@ParametersAreNonnullByDefault
public class GetSelectedOption implements Command<SelenideElement> {
  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource selectElement, @Nullable Object[] args) {
    return wrap(SelenideElement.class, selectElement.driver(), selectElement.description() + " :selected", selectElement,
      "return arguments[0].options[arguments[0].selectedIndex];");
  }
}
