package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Context;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
import static java.util.Collections.singletonList;

public abstract class WebElementSource {
  public abstract Context context();
  public abstract WebElement getWebElement();

  public abstract String getSearchCriteria();

  public SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return ElementFinder.wrap(context(), proxy, getSelector(arg), index);
  }

  public List<WebElement> findAll() throws IndexOutOfBoundsException {
    return singletonList(getWebElement());
  }

  public ElementNotFound createElementNotFoundError(Condition condition, Throwable lastError) {
    return new ElementNotFound(getSearchCriteria(), condition, lastError);
  }

  public static By getSelector(Object arg) {
    return arg instanceof By ? (By) arg : By.cssSelector((String) arg);
  }

  public WebElement checkCondition(String prefix, String message, Condition condition, boolean invert) {
    Condition check = invert ? not(condition) : condition;

    Throwable lastError = null;
    WebElement element = null;
    try {
      element = getWebElement();
      if (element != null && check.apply(context(), element)) {
        return element;
      }
    }
    catch (Throwable e) {
      lastError = e;
    }

    if (Cleanup.of.isInvalidSelectorError(lastError)) {
      throw Cleanup.of.wrap(lastError);
    }

    if (element == null) {
      if (!check.applyNull()) {
        throw createElementNotFoundError(check, lastError);
      }
    }
    else if (invert) {
      throw new ElementShouldNot(getSearchCriteria(), prefix, message, condition, context(), element, lastError);
    }
    else {
      throw new ElementShould(getSearchCriteria(), prefix, message, condition, context(), element, lastError);
    }
    return null;
  }

  public WebElement findAndAssertElementIsVisible() {
    return checkCondition("be ", null, visible, false);
  }
}
