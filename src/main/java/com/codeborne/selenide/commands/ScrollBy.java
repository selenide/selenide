package com.codeborne.selenide.commands;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Command;
import com.codeborne.selenide.ScrollByOptions;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.ScrollByOptions.defaultScrollByOptions;
import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.commands.Util.size;
import org.jspecify.annotations.Nullable;

public class ScrollBy implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    ScrollByOptions options = extractOptions(args);
    WebElement webElement = locator.getWebElement();
    int distance = options.getDistance();
    switch (options.getDirection()) {
      case DOWN -> locator.driver().executeJavaScript("arguments[0].scrollBy(0, " + distance + ")", webElement);
      case UP -> locator.driver().executeJavaScript("arguments[0].scrollBy(0, " + -distance + ")", webElement);
      case RIGHT -> locator.driver().executeJavaScript("arguments[0].scrollBy(" + distance + ", 0)", webElement);
      case LEFT -> locator.driver().executeJavaScript("arguments[0].scrollBy(" + -distance + ", 0)", webElement);
      default -> throw new IllegalArgumentException("Unsupported scrollBy arguments: " + Arrays.toString(args));
    }
    return proxy;
  }

  protected ScrollByOptions extractOptions(Object[] args) {
    if (args[0] instanceof ScrollByOptions options)
      return options;
    else
      return defaultScrollByOptions();
  }
}
