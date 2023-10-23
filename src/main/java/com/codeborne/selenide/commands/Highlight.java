package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.HighlightOptions;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.HighlightOptions.DEFAULT;

@ParametersAreNonnullByDefault
public class Highlight implements Command<WebElement> {
  private final JavaScript js = new JavaScript("highlight.js");

  @Nullable
  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    HighlightOptions options = new Arguments(args).ofType(HighlightOptions.class).orElse(DEFAULT);
    js.execute(locator.driver(), locator.getWebElement(), options.style());
    return proxy;
  }
}
