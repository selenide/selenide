package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class CookieWithName implements ObjectCondition<WebDriver> {

  private final String name;

  public CookieWithName(String name) {
    this.name = name;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String description() {
    return String.format("should have a cookie with name \"%s\"", name);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String negativeDescription() {
    return String.format("should not have cookie with name \"%s\"", name);
  }

  @CheckReturnValue
  @Override
  public boolean test(WebDriver webDriver) {
    return Objects.nonNull(webDriver.manage().getCookieNamed(name));
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
    return String.format("cookie with name \"%s\"", name);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(@Nonnull WebDriver webDriver) {
    return "webdriver";
  }
}
