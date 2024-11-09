package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static java.util.Objects.requireNonNull;

public class GetOwnText implements Command<String> {
  private static final JavaScript js = new JavaScript("get-own-text.js");

  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    return getOwnText(locator.driver(), locator.getWebElement());
  }

  public static String getOwnText(Driver driver, WebElement element) {
    return requireNonNull(js.execute(driver, element));
  }
}
