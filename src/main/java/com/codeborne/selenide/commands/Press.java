package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.Keys;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Press implements Command<SelenideElement> {
  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {

    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Please pass one Keys type to press");
    }
    Keys key = ((Keys) args[0]);
    locator.findAndAssertElementIsInteractable().sendKeys(key);
    return proxy;
  }
}
