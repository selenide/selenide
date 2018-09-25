package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class Matches implements Command<Boolean> {
  @Override
  public Boolean execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    Condition condition = (Condition) args[0];
    WebElement element = getElementOrNull(locator);
    if (element != null) {
      return condition.apply(locator.driver(), element);
    }

    return condition.applyNull();
  }

  protected WebElement getElementOrNull(WebElementSource locator) {
    try {
      return locator.getWebElement();
    } catch (WebDriverException | ElementNotFound elementNotFound) {
      if (Cleanup.of.isInvalidSelectorError(elementNotFound))
        throw Cleanup.of.wrap(elementNotFound);
      return null;
    } catch (IndexOutOfBoundsException ignore) {
      return null;
    } catch (RuntimeException e) {
      throw Cleanup.of.wrap(e);
    }
  }
}
