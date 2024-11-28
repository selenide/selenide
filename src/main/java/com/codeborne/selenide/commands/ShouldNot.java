package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.commands.Util.argsToConditions;

public class ShouldNot extends FluentCommand {
  private final String prefix;

  public ShouldNot() {
    this("");
  }

  protected ShouldNot(String prefix) {
    this.prefix = prefix;
  }

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    for (WebElementCondition condition : argsToConditions(args)) {
      locator.checkCondition(prefix, condition, true);
    }
  }
}
