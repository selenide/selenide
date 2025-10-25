package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

public class Unhighlight extends FluentCommand {
  private final JavaScript js = new JavaScript("unhighlight.js");

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    js.execute(locator.driver(), locator.getWebElement());
  }
}
