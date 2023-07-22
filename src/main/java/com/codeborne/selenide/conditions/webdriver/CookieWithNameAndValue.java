package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class CookieWithNameAndValue implements ObjectCondition<WebDriver> {

  private final String name;
  private final String value;

  public CookieWithNameAndValue(String name, String value) {
    this.name = name;
    this.value = value;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String description() {
    return String.format("should have a cookie with name \"%s\" and value \"%s\"", name, value);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String negativeDescription() {
    return String.format("should not have cookie with name \"%s\" and value \"%s\"", name, value);
  }

  @CheckReturnValue
  @Override
  public boolean test(WebDriver webDriver) {
    Cookie cookie = webDriver.manage().getCookieNamed(name);
    return cookie != null && Objects.equals(cookie.getValue(), value);
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
    return String.format("cookie with name \"%s\" and value \"%s\"", name, value);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(@Nonnull WebDriver webDriver) {
    return "webdriver";
  }
}
