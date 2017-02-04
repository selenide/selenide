package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class IsDisplayed implements Command<Boolean> {
  @Override
  public Boolean execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    try {
      WebElement element = locator.getWebElement();
      return element != null && element.isDisplayed();
    } catch (WebDriverException | ElementNotFound elementNotFound) {
      if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
        throw Cleanup.of.wrap(elementNotFound);
      }
      return false;
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return false;
    }
  }
}
