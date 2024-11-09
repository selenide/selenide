package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.impl.WebElementWrapper.wrap;

public class CacheSelenideElement implements Command<WebElement> {
  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    return wrap(locator.driver(), locator.getWebElement(), locator.getSearchCriteria());
  }
}
