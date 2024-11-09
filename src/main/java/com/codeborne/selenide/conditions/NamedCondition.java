package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public class NamedCondition extends WebElementCondition {
  private final String prefix;
  private final WebElementCondition delegate;

  public NamedCondition(String prefix, WebElementCondition delegate) {
    super(delegate.getName(), delegate.missingElementSatisfiesCondition());
    this.prefix = prefix;
    this.delegate = delegate;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    return delegate.check(driver, element);
  }

  @Override
  public WebElementCondition negate() {
    return delegate.negate();
  }

  @Override
  public String toString() {
    return prefix + ' ' + delegate;
  }
}
