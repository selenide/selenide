package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.Describe;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebDriverException;

public class ToString implements Command<String> {
  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    try {
      return Describe.describe(locator.driver(), locator.getWebElement());
    } catch (WebDriverException | ElementNotFound elementDoesNotExist) {
      return Cleanup.of.webdriverExceptionMessage(elementDoesNotExist);
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return invalidElementIndex.toString();
    }
  }
}
