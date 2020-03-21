package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.io.IOException;

public class Execute<ReturnType> implements Command<ReturnType> {

  @SuppressWarnings("unchecked")
  @Override
  public ReturnType execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    Command<ReturnType> command = (Command<ReturnType>) args[0];
    try {
      return command.execute(proxy, locator, args);
    } catch (IOException e) {
      throw new RuntimeException("Unable to execute custom command", e);
    }
  }
}
