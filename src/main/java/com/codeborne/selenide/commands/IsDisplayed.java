package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class IsDisplayed implements Command<Boolean> {
  @Override
  @SuppressWarnings("ErrorNotRethrown")
  public Boolean execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    try {
      WebElement element = locator.getWebElement();
      return element.isDisplayed();
    }
    catch (WebDriverException | ElementNotFound elementNotFound) {
      if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
        throw Cleanup.of.wrapInvalidSelectorException(elementNotFound);
      }
      return false;
    }
    catch (IndexOutOfBoundsException invalidElementIndex) {
      return false;
    }
  }
}
