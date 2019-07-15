package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import static com.codeborne.selenide.commands.Util.argsToConditions;

public class ShouldNot implements Command<SelenideElement> {
  private final String prefix;

  public ShouldNot() {
    this("");
  }

  protected ShouldNot(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    for (Condition condition : argsToConditions(args)) {
      locator.checkCondition(prefix, condition, true);
    }
    return proxy;
  }
}
