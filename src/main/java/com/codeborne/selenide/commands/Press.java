package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class Press implements Command<SelenideElement> {
  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {

    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Please pass one Keys type to press");
    }
    CharSequence[] key = firstOf(args);
    locator.findAndAssertElementIsInteractable().sendKeys(key);
    return proxy;
  }
}
