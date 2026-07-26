package com.codeborne.selenide.conditions.cookiestore;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.CookieStore;
import com.codeborne.selenide.ObjectCondition;
import org.jspecify.annotations.Nullable;

public class CookieWithNameAndValue implements ObjectCondition<CookieStore> {
  private final com.codeborne.selenide.conditions.webdriver.CookieWithNameAndValue delegate;

  public CookieWithNameAndValue(String name, String value) {
    this.delegate = new com.codeborne.selenide.conditions.webdriver.CookieWithNameAndValue(name, value);
  }

  @Override
  public String description() {
    return delegate.description();
  }

  @Override
  @Nullable
  public String expectedValue() {
    return delegate.expectedValue();
  }

  @Override
  public CheckResult check(CookieStore store) {
    return delegate.check(store.driver().getWebDriver());
  }

  @Override
  public String describe(CookieStore store) {
    return "cookieStore";
  }
}
