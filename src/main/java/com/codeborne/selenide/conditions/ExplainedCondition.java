package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

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

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return delegate.apply(driver, element);
  }

  @Nonnull
  @Override
  public Condition negate() {
    return delegate.negate().because(message);
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return delegate.actualValue(driver, element);
  }

  @Override
  public String toString() {
    return delegate.toString() + " (because " + message + ")";
  }
}
