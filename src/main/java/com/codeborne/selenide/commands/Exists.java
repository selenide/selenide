package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Exists implements Command<Boolean> {
  @Override
  @CheckReturnValue
  @Nonnull
  public Boolean execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    try {
      WebElement webElement = locator.getWebElement();
      if (webElement instanceof SelenideElement se) {
        //noinspection ResultOfMethodCallIgnored
        se.toWebElement();
        return true;
      }
      return true;
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
