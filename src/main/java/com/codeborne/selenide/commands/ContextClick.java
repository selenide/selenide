package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

public class ContextClick extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    locator.driver().actions().contextClick(locator.findAndAssertElementIsInteractable()).perform();
  }
}
