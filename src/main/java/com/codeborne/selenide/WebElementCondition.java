package com.codeborne.selenide;

import com.codeborne.selenide.conditions.ExplainedCondition;
import com.codeborne.selenide.conditions.Not;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

public abstract class WebElementCondition {
  protected final String name;
  protected final boolean missingElementSatisfiesCondition;

  protected WebElementCondition(String name) {
    this(name, false);
  }

  protected WebElementCondition(String name, boolean missingElementSatisfiesCondition) {
    this.name = name;
    this.missingElementSatisfiesCondition = missingElementSatisfiesCondition;
  }

  /**
   * Check if given element matches this condition.
   *
   * @param element given WebElement
   * @return true if element matches condition
   * @deprecated replace by {@link #check(Driver, WebElement)}
   */
  @Deprecated
  public boolean apply(Driver driver, WebElement element) {
    throw new UnsupportedOperationException("Method 'apply' is deprecated. Please implement 'check' method.");
  }

  /**
   * Check if given element matches this condition
   *
   * @param driver  selenide driver
   * @param element given WebElement
   * @return {@link CheckResult.Verdict#ACCEPT} if element matches condition, or
   *         {@link CheckResult.Verdict#REJECT} if element doesn't match (and we should keep trying until timeout).
   *
   * @since 6.0.0
   */
  @Nonnull
  @CheckReturnValue
  public CheckResult check(Driver driver, WebElement element) {
    boolean result = apply(driver, element);
    return new CheckResult(result ? ACCEPT : REJECT, null);
  }

  /**
   * If element didn't match the condition, returns the actual value of element.
   * Used in error reporting.
   * Optional. Makes sense only if you need to add some additional important info to error message.
   *
   * @param driver  given driver
   * @param element given WebElement
   * @return any string that needs to be appended to error message.
   * @deprecated not needed anymore since the actual value is returned by method {@link #check(Driver, WebElement)}
   */
  @Nullable
  @Deprecated
  public String actualValue(Driver driver, WebElement element) {
    return null;
  }

  @Nonnull
  @CheckReturnValue
  public WebElementCondition negate() {
    return new Not(this, missingElementSatisfiesCondition);
  }

  /**
   * Should be used for explaining the reason of condition
   */
  @Nonnull
  @CheckReturnValue
  public WebElementCondition because(String message) {
    return new ExplainedCondition(this, message);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return name;
  }

  @Nonnull
  @CheckReturnValue
  public String getName() {
    return name;
  }

  @CheckReturnValue
  public boolean missingElementSatisfiesCondition() {
    return missingElementSatisfiesCondition;
  }
}
