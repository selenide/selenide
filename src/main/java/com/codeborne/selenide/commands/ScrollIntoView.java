package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.commands.Util.firstOf;

public class ScrollIntoView extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    Object param = firstOf(args);
    WebElement webElement = locator.getWebElement();
    locator.driver().executeJavaScript("arguments[0].scrollIntoView(" + param + ")", webElement);
  }
}
