package com.codeborne.selenide.impl;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.conditions.And;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.UIAssertionError;
import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.Condition.editable;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.impl.Alias.NONE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public abstract class WebElementSource {
  @Nonnull
  private Alias alias = NONE;

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
    this.alias = new Alias(alias);
  }

  @CheckReturnValue
  @Nonnull
  public Alias getAlias() {
    return alias;
  }

  @CheckReturnValue
  @Nonnull
  public String description() {
    return alias.getOrElse(this::getSearchCriteria);
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
    return ElementFinder.wrap(driver(), this, getSelector(arg), index);
  }

  @CheckReturnValue
  @Nonnull
  public List<WebElement> findAll() throws IndexOutOfBoundsException {
    return singletonList(getWebElement());
  }

  @CheckReturnValue
  @Nonnull
  public ElementNotFound createElementNotFoundError(Condition condition, Throwable cause) {
    if (cause instanceof UIAssertionError) {
      throw new IllegalArgumentException("Unexpected UIAssertionError as a cause of ElementNotFound: " + cause, cause);
    }
    return new ElementNotFound(driver(), alias, getSearchCriteria(), condition, cause);
  }

  @CheckReturnValue
  @Nonnull
  public static By getSelector(Object arg) {
    if (arg instanceof By by) return by;
    if (arg instanceof String cssSelector) return By.cssSelector(cssSelector);
    throw new IllegalArgumentException("Unsupported selector type: " + arg);
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void checkCondition(String prefix, Condition condition, boolean invert) {
    checkConditionAndReturnElement(prefix, condition, invert);
  }

  @Nullable
  @CheckReturnValue
  private WebElement checkConditionAndReturnElement(String prefix, Condition condition, boolean invert) {
    Condition check = invert ? not(condition) : condition;

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

  private WebElement handleError(String prefix, Condition condition, boolean invert, Condition check,
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

  /**
   * Asserts that returned element can be interacted with.
   *
   * @return element or throws ElementShould/ElementShouldNot exceptions
   */
  @Nonnull
  @CheckReturnValue
  public WebElement findAndAssertElementIsInteractable() {
    return requireNonNull(checkConditionAndReturnElement("be ", interactable, false));
  }

  /**
   * Asserts that returned element is enabled and can be interacted with.
   *
   * @return element or throws ElementShould/ElementShouldNot exceptions
   * @since 6.15.0
   */
  @Nonnull
  @CheckReturnValue
  public WebElement findAndAssertElementIsClickable() {
    return requireNonNull(checkConditionAndReturnElement("be ", new And("clickable", asList(interactable, enabled)), false));
  }

  /**
   * Asserts that returned element is editable.
   * @return element or throws ElementShould/ElementShouldNot exceptions
   * @since 6.5.0
   */
  @Nonnull
  @CheckReturnValue
  public WebElement findAndAssertElementIsEditable() {
    return requireNonNull(checkConditionAndReturnElement("be ", editable, false));
  }
}
