package com.codeborne.selenide.impl;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.UIAssertionError;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.editable;
import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.impl.Alias.NONE;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

public abstract class WebElementSource {
  private Alias alias = NONE;

  public abstract Driver driver();

  public abstract WebElement getWebElement();

  public abstract String getSearchCriteria();

  public void setAlias(String alias) {
    this.alias = new Alias(alias);
  }

  public Alias getAlias() {
    return alias;
  }

  public String description() {
    return alias.getOrElse(this::getSearchCriteria);
  }

  @Override
  public String toString() {
    return description();
  }

  public SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return ElementFinder.wrap(driver(), this, getSelector(arg), index);
  }

  public List<WebElement> findAll() throws IndexOutOfBoundsException {
    return singletonList(getWebElement());
  }

  public ElementNotFound createElementNotFoundError(WebElementCondition condition, @Nullable Throwable cause) {
    if (cause instanceof UIAssertionError) {
      throw new IllegalArgumentException("Unexpected UIAssertionError as a cause of ElementNotFound: " + cause, cause);
    }
    return new ElementNotFound(alias, getSearchCriteria(), condition, cause);
  }

  public static By getSelector(Object arg) {
    if (arg instanceof By by) return by;
    if (arg instanceof String cssSelector) return By.cssSelector(cssSelector);
    throw new IllegalArgumentException("Unsupported selector type: " + arg);
  }

  public void checkCondition(String prefix, WebElementCondition condition, boolean invert) {
    checkConditionAndReturnElement(prefix, condition, invert);
  }

  @Nullable
  @CanIgnoreReturnValue
  @SuppressWarnings("ErrorNotRethrown")
  private WebElement checkConditionAndReturnElement(String prefix, WebElementCondition condition, boolean invert) {
    WebElementCondition check = invert ? not(condition) : condition;

    Throwable lastError = null;
    WebElement element = null;
    CheckResult checkResult = null;
    try {
      element = getWebElement();
      checkResult = check.check(driver(), element);

      if (checkResult.verdict() == ACCEPT) {
        return element;
      }
    }
    catch (WebDriverException | IndexOutOfBoundsException | AssertionError e) {
      lastError = e;
    }

    return handleError(prefix, condition, invert, check, lastError, element, checkResult);
  }

  @Nullable
  private WebElement handleError(String prefix, WebElementCondition condition, boolean invert, WebElementCondition check,
                                 @Nullable Throwable lastError, @Nullable WebElement element, @Nullable CheckResult checkResult) {
    if (lastError != null && Cleanup.of.isInvalidSelectorError(lastError)) {
      throw Cleanup.of.wrapInvalidSelectorException(lastError);
    }
    if (lastError instanceof UnhandledAlertException unhandledAlertException) {
      throw unhandledAlertException;
    }

    if (element == null) {
      if (check.missingElementSatisfiesCondition()) {
        return null;
      }
      throw createElementNotFoundError(check, lastError);
    }
    else if (invert) {
      throw new ElementShouldNot(driver(), getAlias(), getSearchCriteria(), prefix, condition, checkResult, element, lastError);
    }
    else {
      throw new ElementShould(driver(), getAlias(), getSearchCriteria(), prefix, condition, checkResult, element, lastError);
    }
  }

  public WebElement findAndAssertElementIsVisible() {
    return requireNonNull(checkConditionAndReturnElement("be ", visible, false));
  }

  /**
   * Asserts that returned element can be interacted with.
   *
   * @return element or throws ElementShould/ElementShouldNot exceptions
   */
  public WebElement findAndAssertElementIsInteractable() {
    return requireNonNull(checkConditionAndReturnElement("be ", interactable, false));
  }

  /**
   * Asserts that returned element is enabled and can be interacted with.
   *
   * @return element or throws ElementShould/ElementShouldNot exceptions
   */
  public WebElement findAndAssertElementIsClickable() {
    return requireNonNull(checkConditionAndReturnElement("be ", clickable, false));
  }

  /**
   * Asserts that returned element is editable.
   * @return element or throws ElementShould/ElementShouldNot exceptions
   */
  public WebElement findAndAssertElementIsEditable() {
    return requireNonNull(checkConditionAndReturnElement("be ", editable, false));
  }
}
