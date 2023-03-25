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
public class GetInnerText implements Command<String> {
  @Override
  @CheckReturnValue
  @Nonnull
  public String execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    WebElement element = locator.getWebElement();
    return getInnerText(locator.driver(), element);
  }

  public static String getInnerText(Driver driver, WebElement element) {
    if (driver.browser().isIE()) {
      return element.getAttribute("innerText");
    }
    return element.getAttribute("textContent");
  }
}
