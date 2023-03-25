package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.impl.WebElementWrapper.wrap;

@ParametersAreNonnullByDefault
public class CacheSelenideElement implements Command<WebElement> {
  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    return wrap(locator.driver(), locator.getWebElement(), locator.getSearchCriteria());
  }
}
