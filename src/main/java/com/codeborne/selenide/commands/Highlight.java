package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.HighlightOptions;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.HighlightOptions.DEFAULT;

public class Highlight extends FluentCommand {
  private final JavaScript js = new JavaScript("highlight.js");

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    HighlightOptions options = new Arguments(args).ofType(HighlightOptions.class).orElse(DEFAULT);
    js.execute(locator.driver(), locator.getWebElement(), options.style());
  }
}
