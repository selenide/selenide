package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static java.util.Objects.requireNonNull;

public class GetInnerText implements Command<String> {
  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    WebElement element = locator.getWebElement();
    return getInnerText(locator.driver(), element);
  }

  public static String getInnerText(Driver driver, WebElement element) {
    if (driver.browser().isIE()) {
      return requireNonNull(element.getAttribute("innerText"));
    }
    return requireNonNull(element.getAttribute("textContent"));
  }
}
