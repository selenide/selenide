package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GetOwnText implements Command<String> {
  @Override
  @CheckReturnValue
  @Nonnull
  public String execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    return getOwnText(locator.driver(), locator.getWebElement());
  }

  @Nonnull
  public static String getOwnText(Driver driver, WebElement element) {
    return driver.executeJavaScript("return Array.prototype.filter.call(arguments[0].childNodes, function (element) {\n" +
      "  return element.nodeType === Node.TEXT_NODE;\n" +
      "}).map(function (element) {\n" +
      "  return element.textContent;\n" +
      "}).join(\"\\n\");", element);
  }
}
