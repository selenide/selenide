package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GetSearchCriteria implements Command<String> {
  @Override
  @Nonnull
  @CheckReturnValue
  public String execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    return locator.getSearchCriteria();
  }
}
