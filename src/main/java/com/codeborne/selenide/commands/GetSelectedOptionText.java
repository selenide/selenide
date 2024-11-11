package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

public class GetSelectedOptionText implements Command<String> {
  private final JavaScript js = new JavaScript("get-selected-option-text.js");

  @Override
  public String execute(SelenideElement proxy, WebElementSource selectElement, Object @Nullable [] args) {
    return execute(selectElement.driver(), selectElement.getWebElement());
  }

  public String execute(Driver driver, WebElement webElement) {
    return js.executeOrFail(driver, webElement);
  }
}
