package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GetClosestWithAttribute implements Command<SelenideElement> {

  @Override
  @Nullable
  @CheckReturnValue
  public SelenideElement execute(
    @Nonnull SelenideElement proxy,
    @Nonnull WebElementSource locator,
    @Nullable Object[] args
  ) {
    return new GetClosestWithAttributeAndValue().execute(proxy, locator, args);
  }
}
