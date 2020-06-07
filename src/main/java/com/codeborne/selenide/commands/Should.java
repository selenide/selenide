package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.argsToConditions;

@ParametersAreNonnullByDefault
public class Should implements Command<SelenideElement> {
  private final String prefix;

  public Should() {
    this("");
  }

  protected Should(String prefix) {
    this.prefix = prefix;
  }

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    for (Condition condition : argsToConditions(args)) {
      locator.checkCondition(prefix, condition, false);
    }
    return proxy;
  }
}
