package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
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
  private boolean isXpath;

  public abstract WebElement getWebElement();

  public abstract String getSearchCriteria();

  public void setXpath(boolean isXpath) {
    this.isXpath = isXpath;
  }

  public boolean isXpath() {
    return this.isXpath;
  }

  public SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return ElementFinder.wrap(proxy, getSelector(arg, this.isXpath), index);
  }

  public List<WebElement> findAll() throws IndexOutOfBoundsException {
    return singletonList(getWebElement());
  }

  public ElementNotFound createElementNotFoundError(Condition condition, Throwable lastError) {
    return new ElementNotFound(getSearchCriteria(), condition, lastError);
  }

  public static By getSelector(Object arg) {
    return getSelector(arg, false);
  }

  public static By getSelector(Object arg, boolean isXpath) {
    return arg instanceof By ? (By) arg : (isXpath ? By.xpath((String) arg) : By.cssSelector((String) arg));
  }

  public WebElement checkCondition(String prefix, String message, Condition condition, boolean invert) {
    Condition check = invert ? not(condition) : condition;

    Throwable lastError = null;
    WebElement element = null;
    try {
      element = getWebElement();
      if (element != null && check.apply(element)) {
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
      throw new ElementShouldNot(getSearchCriteria(), prefix, message, condition, element, lastError);
    }
    else {
      throw new ElementShould(getSearchCriteria(), prefix, message, condition, element, lastError);
    }
    return null;
  }

  public WebElement findAndAssertElementIsVisible() {
    return checkCondition("be ", null, visible, false);
  }
}
