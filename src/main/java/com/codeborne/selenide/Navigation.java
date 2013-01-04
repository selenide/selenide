package com.codeborne.selenide;

import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.DOM.waitFor;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.ie;

public class Navigation {
  public static String baseUrl = "http://localhost:8080";

  public static void open(String relativeUrl) {
    navigateToAbsoluteUrl(absoluteUrl(relativeUrl));
  }

  public static String absoluteUrl(String relativeUrl) {
    return baseUrl + relativeUrl;
  }

  public static void navigateToAbsoluteUrl(URL url) {
    navigateToAbsoluteUrl(url.toExternalForm());
  }

  public static void navigateToAbsoluteUrl(String url) {
    if (ie()) {
      getWebDriver().navigate().to(makeUniqueUrlToAvoidIECaching(url, System.nanoTime()));
      waitFor(By.tagName("body"));
      toBeSureThatPageIsNotCached();
    }
    else {
      getWebDriver().navigate().to(url);
      waitFor(By.tagName("body"));
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
