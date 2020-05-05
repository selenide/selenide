package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

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

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return delegate.apply(driver, element);
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return delegate.actualValue(driver, element);
  }

  @Nonnull
  @Override
  public Condition negate() {
    return delegate.negate();
  }

  @Override
  public String toString() {
    return prefix + ' ' + delegate.toString();
  }
}
