package com.codeborne.selenide;

import com.codeborne.selenide.conditions.webdriver.*;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

/**
 * @since 5.23.0
 */
public class WebDriverConditions {
  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<WebDriver> url(String expectedUrl) {
    return new Url(expectedUrl);
  }

  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<WebDriver> urlStartingWith(String expectedUrl) {
    return new UrlStartingWith(expectedUrl);
  }

  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<WebDriver> urlContaining(String expectedUrl) {
    return new UrlContaining(expectedUrl);
  }

  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<WebDriver> currentFrameUrl(String expectedUrl) {
    return new CurrentFrameUrl(expectedUrl);
  }

  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<WebDriver> currentFrameUrlStartingWith(String expectedUrl) {
    return new CurrentFrameUrlStartingWith(expectedUrl);
  }

  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<WebDriver> currentFrameUrlContaining(String expectedUrl) {
    return new CurrentFrameUrlContaining(expectedUrl);
  }

  /**
   * Check that the number of windows/tabs in the browser is as expected.
   * Example:
   * {@code webdriver().shouldHave(numberOfWindows(2)) }
   */
  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<WebDriver> numberOfWindows(int numberOfWindows) {
    return new NumberOfWindows(numberOfWindows);
  }

  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<WebDriver> title(String expectedTitle) {
    return new TitleContaining(expectedTitle);
  }
}
