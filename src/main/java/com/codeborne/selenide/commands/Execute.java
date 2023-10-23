package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class Execute<ReturnType> implements Command<ReturnType> {

  @Override
  public ReturnType execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    Command<ReturnType> command = firstOf(args);
    return command.execute(proxy, locator, NO_ARGS);
  }
}
