package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.commands.Util.firstOf;

public class Execute<ReturnType> implements Command<ReturnType> {
  @Nullable
  @CanIgnoreReturnValue
  @Override
  public ReturnType execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    Command<ReturnType> command = firstOf(args);
    return command.execute(proxy, locator, NO_ARGS);
  }
}
