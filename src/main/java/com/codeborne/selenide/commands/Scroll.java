package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.ScrollOptions;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ScrollOptions.defaultScrollOptions;

public class Scroll extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object[] args) {
    ScrollOptions options = extractOptions(args);
    WebElement webElement = locator.getWebElement();
    int distance = options.distance();
    String js = switch (options.direction()) {
      case DOWN -> "arguments[0].scrollBy(0, arguments[1])";
      case UP -> "arguments[0].scrollBy(0, -arguments[1])";
      case RIGHT -> "arguments[0].scrollBy(arguments[1], 0)";
      case LEFT -> "arguments[0].scrollBy(-arguments[1], 0)";
    };
    locator.driver().executeJavaScript(js, webElement, distance);
  }

  protected ScrollOptions extractOptions(Object[] args) {
    if (args[0] instanceof ScrollOptions options)
      return options;
    else
      return defaultScrollOptions();
  }
}
