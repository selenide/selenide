package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.io.IOException;

public class CustomCommand implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    Command command = (Command) args[0];
    try {
      command.execute(proxy, locator, args);
    } catch (IOException e) {
      throw new RuntimeException("Unable to execute custom command", e);
    }
    return proxy;
  }
}
