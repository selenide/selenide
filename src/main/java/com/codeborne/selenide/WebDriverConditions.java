package com.codeborne.selenide;

import com.codeborne.selenide.conditions.webdriver.CurrentFrameUrl;
import com.codeborne.selenide.conditions.webdriver.CurrentFrameUrlContaining;
import com.codeborne.selenide.conditions.webdriver.CurrentFrameUrlStartingWith;
import com.codeborne.selenide.conditions.webdriver.Url;
import com.codeborne.selenide.conditions.webdriver.UrlContaining;
import com.codeborne.selenide.conditions.webdriver.UrlStartingWith;
import org.openqa.selenium.WebDriver;

/**
 * @since 5.23.0
 */
public class WebDriverConditions {
  public static ObjectCondition<WebDriver> url(String expectedUrl) {
    return new Url(expectedUrl);
  }

  public static ObjectCondition<WebDriver> urlStartingWith(String expectedUrl) {
    return new UrlStartingWith(expectedUrl);
  }

  public static ObjectCondition<WebDriver> urlContaining(String expectedUrl) {
    return new UrlContaining(expectedUrl);
  }

  public static ObjectCondition<WebDriver> currentFrameUrl(String expectedUrl) {
    return new CurrentFrameUrl(expectedUrl);
  }

  public static ObjectCondition<WebDriver> currentFrameUrlStartingWith(String expectedUrl) {
    return new CurrentFrameUrlStartingWith(expectedUrl);
  }

  public static ObjectCondition<WebDriver> currentFrameUrlContaining(String expectedUrl) {
    return new CurrentFrameUrlContaining(expectedUrl);
  }
}
