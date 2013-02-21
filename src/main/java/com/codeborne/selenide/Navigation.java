package com.codeborne.selenide;

import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.ie;

public class Navigation {
  public static String baseUrl = "http://localhost:8080";

  /**
   * The main starting point in your tests.
   * Open a browser window with given URL.
   *
   * If browser window was already opened before, it will be reused.
   *
   * Don't bother about closing the browser - it will be closed automatically when all your tests are done.
   *
   * @param relativeOrAbsoluteUrl If starting with "http://" or "https://" or "file://", it's considered to be relative URL. In this case, it's prepended by baseUrl
   */
  public static void open(String relativeOrAbsoluteUrl) {
    if (relativeOrAbsoluteUrl.startsWith("http://") ||
        relativeOrAbsoluteUrl.startsWith("https://") ||
        relativeOrAbsoluteUrl.startsWith("file://")) {
      navigateToAbsoluteUrl(relativeOrAbsoluteUrl);
    } else {
      navigateToAbsoluteUrl(absoluteUrl(relativeOrAbsoluteUrl));
    }
  }

  public static String absoluteUrl(String relativeUrl) {
    return baseUrl + relativeUrl;
  }

  /**
   * @deprecated Use Selenide.open(url)
   */
  @Deprecated
  public static void navigateToAbsoluteUrl(URL url) {
    navigateToAbsoluteUrl(url.toExternalForm());
  }

  /**
   * @deprecated Use Selenide.open(url)
   */
  @Deprecated
  public static void navigateToAbsoluteUrl(String url) {
    if (ie()) {
      getWebDriver().navigate().to(makeUniqueUrlToAvoidIECaching(url, System.nanoTime()));
      $(By.tagName("body")).should(appear);
      toBeSureThatPageIsNotCached();
    }
    else {
      getWebDriver().navigate().to(url);
      $(By.tagName("body")).should(appear);
    }
  }

  private static void toBeSureThatPageIsNotCached() {
    String currentUrl = getWebDriver().getCurrentUrl();
    if (!currentUrl.contains("timestamp=")) {
      navigateToAbsoluteUrl(currentUrl);
    }
  }

  static String makeUniqueUrlToAvoidIECaching(String url, long unique) {
    final String fullUrl;
    if (url.contains("timestamp=")) {
      fullUrl = url.replaceFirst("(.*)(timestamp=)(.*)([&#].*)", "$1$2" + unique + "$4")
          .replaceFirst("(.*)(timestamp=)(.*)$", "$1$2" + unique);
    } else {
      fullUrl = url.contains("?") ?
          url + "&timestamp=" + unique :
          url + "?timestamp=" + unique;
    }
    return fullUrl;
  }

  /**
   * Assert that URL of current page is #baseUrl + #relativeUrl
   * @param relativeUrl expected relative url like "/petclinic/user/login"
   */
  public static void assertURL(String relativeUrl) {
    String expectedUrl = baseUrl + relativeUrl;
    String actualUrl = getWebDriver().getCurrentUrl().replaceFirst("\\?.*$", "");
    if (!expectedUrl.equals(actualUrl)) {
      throw new AssertionError("Actual URL " + actualUrl + " does not match with expected " + expectedUrl);
    }
  }

  public static String source() {
    return getWebDriver().getPageSource();
  }

  public static String title() {
    return getWebDriver().getTitle();
  }

  public static String url() {
    return getWebDriver().getCurrentUrl();
  }

  /**
   * Reload current page
   */
  public static void refresh() {
    navigateToAbsoluteUrl(url());
  }

  /**
   * Not recommended. Test should not sleep, but should wait for some condition instead.
   * @param milliseconds Time to sleep in milliseconds
   */
  public static void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
