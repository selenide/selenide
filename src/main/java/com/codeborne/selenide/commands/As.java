package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;

import static java.util.Objects.requireNonNull;

public class As extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object[] args) {
    locator.setAlias((String) requireNonNull(args)[0]);
  }
}
