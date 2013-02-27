package com.codeborne.selenide;

import java.net.URL;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * @deprecated Use methods of class Selenide
 */
@Deprecated
public class Navigation {
  /**
   * If you want to set "selenide.baseUrl" property programmatically, please use Configuration.baseUrl
   */
  @Deprecated
  public static String baseUrl = "http://localhost:8080";

  /**
   * @deprecated Use Selenide.open(relativeOrAbsoluteUrl)
   */
  @Deprecated
  public static void open(String relativeOrAbsoluteUrl) {
    Selenide.open(relativeOrAbsoluteUrl);
  }

  @Deprecated
  public static String absoluteUrl(String relativeUrl) {
    return Configuration.getBaseUrl() + relativeUrl;
  }

  /**
   * @deprecated Use Selenide.open(url)
   */
  @Deprecated
  public static void navigateToAbsoluteUrl(URL url) {
    Selenide.open(url);
  }

  /**
   * @deprecated Use Selenide.open(url)
   */
  @Deprecated
  public static void navigateToAbsoluteUrl(String url) {
    Selenide.open(url);
  }

  /**
   * @deprecated Do not check URLs as user does not
   */
  @Deprecated
  public static void assertURL(String relativeUrl) {
    String expectedUrl = Configuration.getBaseUrl() + relativeUrl;
    String actualUrl = getWebDriver().getCurrentUrl().replaceFirst("\\?.*$", "");
    if (!expectedUrl.equals(actualUrl)) {
      throw new AssertionError("Actual URL " + actualUrl + " does not match with expected " + expectedUrl);
    }
  }

  /**
   * @deprecated Use WebDriverRunner.source()
   */
  @Deprecated
  public static String source() {
    return getWebDriver().getPageSource();
  }

  /**
   * @deprecated Use Selenide.title()
   */
  @Deprecated
  public static String title() {
    return getWebDriver().getTitle();
  }

  /**
   * @deprecated Use WebDriverRunner.url()
   */
  @Deprecated
  public static String url() {
    return getWebDriver().getCurrentUrl();
  }

  /**
   * @deprecated Use Selenide.refresh()
   */
  @Deprecated
  public static void refresh() {
    Selenide.refresh();
  }

  /**
   * @deprecated Use Selenide.sleep()
   */
  @Deprecated
  public static void sleep(long milliseconds) {
    Selenide.sleep(milliseconds);
  }
}
