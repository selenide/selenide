package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GetOwnText implements Command<String> {
  private static final JavaScript js = new JavaScript("get-own-text.js");

  @Override
  @CheckReturnValue
  @Nonnull
  public String execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    return getOwnText(locator.driver(), locator.getWebElement());
  }

  @Nonnull
  public static String getOwnText(Driver driver, WebElement element) {
    return js.execute(driver, element);
  }
}
