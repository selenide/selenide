package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.util.List;

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
    if (args[0] instanceof String) {
      shouldNot(proxy, locator, (String) args[0], argsToConditions(args));
    }
    else {
      shouldNot(proxy, locator, null, argsToConditions(args));
    }
    return proxy;
  }

  protected void shouldNot(SelenideElement proxy, WebElementSource locator, String message, List<Condition> conditions) {
    for (Condition condition : conditions) {
      locator.checkCondition(prefix, message, condition, true);
    }
  }
}
