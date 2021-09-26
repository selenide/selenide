package com.codeborne.selenide.commands;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.time.Duration;

@ParametersAreNonnullByDefault
public class Click implements Command<Void> {
  private static final Command<Void> OVERLOADED_METHOD_COMMAND = new OverloadedMethodCommandBuilder<Void>(3)
    .withRule(new Class[][]{
        {},
        {Duration.class}
      },
      (proxy, locator, args) -> {
        ClickOptions.clickUsingConfigMethod(
          locator.driver(),
          locator.findAndAssertElementIsInteractable()
        );
        return null;
      }
    ).withRule(new Class[][]{
        {Integer.class, Integer.class},
        {Integer.class, Integer.class, Duration.class}
      },
      (proxy, locator, args) -> {
        ClickOptions.clickUsingConfigMethod(
          locator.driver(),
          locator.findAndAssertElementIsInteractable(),
          (Integer) args[0], (Integer) args[1]
        );
        return null;
      }
    ).withRule(new Class[][]{
        {ClickOptions.class},
        {ClickOptions.class, Duration.class}
      },
      (proxy, locator, args) -> {
        ((ClickOptions) args[0]).click(
          locator.driver(),
          locator.findAndAssertElementIsInteractable()
        );
        return null;
      }
    ).build();

  @Override
  @Nullable
  public Void execute(SelenideElement proxy,
                      WebElementSource locator,
                      @Nullable Object[] args) throws IOException {
    return OVERLOADED_METHOD_COMMAND.execute(proxy, locator, args);
  }
}
