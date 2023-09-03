package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExplainedCondition<T extends WebElementCondition> extends WebElementCondition {
  private final T delegate;
  private final String message;

  public ExplainedCondition(T delegate, String message) {
    super(delegate.getName(), delegate.missingElementSatisfiesCondition());
    this.delegate = delegate;
    this.message = message;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    return delegate.check(driver, element);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public WebElementCondition negate() {
    return delegate.negate().because(message);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return delegate.toString() + " (because " + message + ")";
  }
}
