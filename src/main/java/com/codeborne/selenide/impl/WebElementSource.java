package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.have;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.visible;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public abstract class WebElementSource {
  @Nullable
  protected String alias;

  @CheckReturnValue
  @Nonnull
  public abstract Driver driver();

  @CheckReturnValue
  @Nonnull
  public abstract WebElement getWebElement();

  @CheckReturnValue
  @Nonnull
  public abstract String getSearchCriteria();

  public void setAlias(String alias) {
    if (alias.isEmpty()) throw new IllegalArgumentException("Empty alias not allowed");
    this.alias = alias;
  }

  @CheckReturnValue
  @Nullable
  public String getAlias() {
    return alias;
  }

  @CheckReturnValue
  @Nonnull
  public String description() {
    return getAlias() != null ? getAlias() : getSearchCriteria();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return description();
  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return ElementFinder.wrap(driver(), proxy, getSelector(arg), index);
  }

  @CheckReturnValue
  @Nonnull
  public List<WebElement> findAll() throws IndexOutOfBoundsException {
    return singletonList(getWebElement());
  }

  @CheckReturnValue
  @Nonnull
  public ElementNotFound createElementNotFoundError(Condition condition, Throwable lastError) {
    return new ElementNotFound(driver(), description(), condition, lastError);
  }

  @CheckReturnValue
  @Nonnull
  public static By getSelector(Object arg) {
    return arg instanceof By ? (By) arg : By.cssSelector((String) arg);
  }

  @Nullable
  public WebElement checkCondition(String prefix, Condition condition, boolean invert) {
    Condition check = invert ? not(condition) : condition;

    Throwable lastError = null;
    WebElement element = null;
    try {
      element = getWebElement();
      if (check.apply(driver(), element)) {
        return element;
      }
    }
    catch (WebDriverException | IndexOutOfBoundsException | AssertionError e) {
      lastError = e;
    }

    if (lastError != null && Cleanup.of.isInvalidSelectorError(lastError)) {
      throw Cleanup.of.wrap(lastError);
    }

    if (element == null) {
      if (!check.applyNull()) {
        throw createElementNotFoundError(check, lastError);
      }
    }
    else if (invert) {
      throw new ElementShouldNot(driver(), description(), prefix, condition, element, lastError);
    }
    else {
      throw new ElementShould(driver(), description(), prefix, condition, element, lastError);
    }
    return null;
  }

  /**
   * Asserts that returned element can be interacted with.
   *
   * Elements which are transparent (opacity:0) are considered to be invisible, but interactable.
   * User (as of 05.12.2018) can click, doubleClick etc., and enter text etc. to transparent elements
   * for all major browsers
   *
   * @return element or throws ElementShould/ElementShouldNot exceptions
   */
  @CheckReturnValue
  @Nonnull
  public WebElement findAndAssertElementIsInteractable() {
    return requireNonNull(
      checkCondition("be ",
        or("visible or transparent", visible, have(cssValue("opacity", "0"))),
        false)
    );
  }
}
