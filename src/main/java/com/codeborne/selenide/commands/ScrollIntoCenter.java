package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.ScrollIntoViewOptions.Block.center;
import static com.codeborne.selenide.ScrollIntoViewOptions.instant;

public class ScrollIntoCenter implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    return proxy.scrollIntoView(instant().block(center));
  }
}
