package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.util.List;

import static com.codeborne.selenide.commands.Util.argsToConditions;

public class Should implements Command<SelenideElement> {
  private final String prefix;

  public Should() {
    this("");
  }

  protected Should(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String message = null;
    if (args[0] instanceof String) {
      message = (String) args[0];
    }
    should(locator, message, argsToConditions(args));
    return proxy;
  }

  protected void should(WebElementSource locator, String message, List<Condition> conditions) {
    for (Condition condition : conditions) {
      locator.checkCondition(prefix, message, condition, false);
    }
  }
}
