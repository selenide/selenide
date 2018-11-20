package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

public class ClickIgnoreVisibility extends Click {
  @Override
  public Void execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    if (args == null || args.length == 0) {
      click(locator.driver(), locator.findAndAssertElementExists());
    } else if (args.length == 2) {
      click(locator.driver(), locator.findAndAssertElementExists(), (int) args[0], (int) args[1]);
    }
    return null;
  }
}
