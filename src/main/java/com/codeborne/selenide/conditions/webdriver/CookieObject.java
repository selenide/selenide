package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CookieObject implements ObjectCondition<WebDriver> {

  private final Cookie cookie;

  public CookieObject(Cookie cookie) {
    this.cookie = cookie;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String description() {
    return String.format("should have cookie \"%s\"", cookie);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String negativeDescription() {
    return String.format("should not have cookie \"%s\"", cookie);
  }

  @CheckReturnValue
  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.manage().getCookies().contains(cookie);
  }

  @Nullable
  @CheckReturnValue
  @Override
  public String actualValue(WebDriver webDriver) {
    return String.format("Available cookies: %s", webDriver.manage().getCookies());
  }

  @Nullable
  @CheckReturnValue
  @Override
  public String expectedValue() {
    return String.format("cookie \"%s\"", cookie);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(@Nonnull WebDriver webDriver) {
    return "webdriver";
  }
}
