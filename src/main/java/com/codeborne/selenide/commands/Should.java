package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.commands.Util.argsToConditions;

public class Should extends FluentCommand {
  private final String prefix;

  public Should() {
    this("");
  }

  protected Should(String prefix) {
    this.prefix = prefix;
  }

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    for (WebElementCondition condition : argsToConditions(args)) {
      locator.checkCondition(prefix, condition, false);
    }
  }
}
