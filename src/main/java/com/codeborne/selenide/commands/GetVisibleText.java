package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static java.util.Objects.requireNonNull;

public class GetVisibleText implements Command<String> {
  private static final JavaScript js = new JavaScript("visible-text.js");

  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    return getVisibleText(locator.driver(), locator.getWebElement());
  }

  public static String getVisibleText(Driver driver, WebElement element) {
    return getVisibleText(driver, element, new GetSelectedOptionText());
  }

  public static String getVisibleText(Driver driver, WebElement element, GetSelectedOptionText getSelectedOptionText) {
    return "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionText.execute(driver, element) :
      requireNonNull(js.execute(driver, element));
  }
}
