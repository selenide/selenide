package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class ScrollIntoView implements Command<WebElement> {
  @Override
  @Nonnull
  public WebElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    Object param = firstOf(args);
    locator.driver().executeJavaScript("arguments[0].scrollIntoView(" + param + ")", proxy);
    return proxy;
  }
}
