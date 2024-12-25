package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.ScrollOptions;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

import static com.codeborne.selenide.ScrollOptions.defaultScrollOptions;

import org.jspecify.annotations.Nullable;

public class Scroll implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    ScrollOptions options = extractOptions(args);
    WebElement webElement = locator.getWebElement();
    int distance = options.getDistance();
    switch (options.getDirection()) {
      case DOWN -> locator.driver().executeJavaScript("arguments[0].scrollBy(0, " + distance + ")", webElement);
      case UP -> locator.driver().executeJavaScript("arguments[0].scrollBy(0, " + -distance + ")", webElement);
      case RIGHT -> locator.driver().executeJavaScript("arguments[0].scrollBy(" + distance + ", 0)", webElement);
      case LEFT -> locator.driver().executeJavaScript("arguments[0].scrollBy(" + -distance + ", 0)", webElement);
      default -> throw new IllegalArgumentException("Unsupported scroll arguments: " + Arrays.toString(args));
    }
    return proxy;
  }

  protected ScrollOptions extractOptions(Object[] args) {
    if (args[0] instanceof ScrollOptions options)
      return options;
    else
      return defaultScrollOptions();
  }
}
