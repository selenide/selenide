package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

public class Find implements Command<SelenideElement> {
  private boolean isXpath;

  Find() {
    this(false);
  }

  Find(boolean isXpath) {
    this.isXpath = isXpath;
  }

  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object... args) {
    locator.setXpath(this.isXpath);
    return args.length == 1 ?
        locator.find(proxy, args[0], 0) :
        locator.find(proxy, args[0], (Integer) args[1]);
  }
}
