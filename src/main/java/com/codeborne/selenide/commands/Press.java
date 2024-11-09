package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.commands.Util.firstOf;

public class Press extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {

    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Please pass one Keys type to press");
    }
    CharSequence[] key = firstOf(args);
    locator.findAndAssertElementIsInteractable().sendKeys(key);
  }
}
