package com.codeborne.selenide;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * @since 5.23.0
 */
public class WebDriverConditions {
  public static Predicate<WebDriver> url(String expectedUrl) {
    return webDriver -> webDriver.getCurrentUrl().equals(expectedUrl);
  }

  public static Predicate<WebDriver> urlStartingWith(String expectedUrl) {
    return webDriver -> webDriver.getCurrentUrl().startsWith(expectedUrl);
  }

  public static Predicate<WebDriver> currentFrameUrl(String expectedUrl) {
    return webDriver -> getCurrentFrameUrl(webDriver).equals(expectedUrl);
  }

  public static Predicate<WebDriver> currentFrameUrlStartingWith(String expectedUrl) {
    return webDriver -> getCurrentFrameUrl(webDriver).startsWith(expectedUrl);
  }

  @CheckReturnValue
  @Nonnull
  private static String getCurrentFrameUrl(WebDriver webDriver) {
    return ((JavascriptExecutor) webDriver).executeScript("return window.location.href").toString();
  }
}
