package com.codeborne.selenide.commands.sibling;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface Sibling {
  @CheckReturnValue
  @Nonnull
  SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args);

  boolean canExecute(@Nonnull Object[] args);
}
