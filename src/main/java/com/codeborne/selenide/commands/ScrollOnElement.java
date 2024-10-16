package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ScrollOnElement implements Command<SelenideElement> {
  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    Arguments arguments = new Arguments(args);
    WebElement webElement = locator.getWebElement();
    if (args.length == 2) {
      locator.driver().executeJavaScript("arguments[0].scrollBy(" + arguments.nth(1) + ", " + arguments.nth(0) + ")", webElement);
    } else if (args.length == 1) {
      locator.driver().executeJavaScript("arguments[0].scrollBy(0, " + arguments.nth(0) + ")", webElement);
    }
    return proxy;
  }
}
