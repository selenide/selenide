package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExplainedCondition extends Condition {
  private final Condition delegate;
  private final String message;

  public ExplainedCondition(Condition delegate, String message) {
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
  public Condition negate() {
    return delegate.negate().because(message);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return delegate.toString() + " (because " + message + ")";
  }
}
