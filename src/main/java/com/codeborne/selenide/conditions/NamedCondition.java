package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NamedCondition extends Condition {
  private final String prefix;
  private final Condition delegate;

  public NamedCondition(String prefix, Condition delegate) {
    super(delegate.getName(), delegate.missingElementSatisfiesCondition());
    this.prefix = prefix;
    this.delegate = delegate;
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
    return delegate.negate();
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return prefix + ' ' + delegate.toString();
  }
}
