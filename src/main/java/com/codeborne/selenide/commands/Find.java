package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

public class Find implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object... args) {
    return args.length == 1 ?
        locator.find(proxy, args[0], 0) :
        locator.find(proxy, args[0], (Integer) args[1]);
  }
}
