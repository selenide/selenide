package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static java.util.Objects.requireNonNull;

public class GetInnerHtml implements Command<String> {
  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    WebElement element = locator.getWebElement();
    return requireNonNull(element.getAttribute("innerHTML"));
  }
}
