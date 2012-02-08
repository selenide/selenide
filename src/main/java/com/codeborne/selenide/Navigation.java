package com.codeborne.selenide;

import org.openqa.selenium.By;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.ie;
import static org.junit.Assert.assertTrue;

public class Navigation {
  public static String baseUrl = "http://localhost:8080";

  public static void open(String relativeUrl) {
    navigateToAbsoluteUrl(baseUrl + relativeUrl);
  }

  public static void navigateToAbsoluteUrl(String url) {
    getWebDriver().navigate().to(makeUniqueUrl(url, System.nanoTime()));
    assertTrue(getWebDriver().findElement(By.tagName("body")).isDisplayed());

    if (ie()) {
      toBeSureThatPageIsNotCached();
    }
  }

  private static void toBeSureThatPageIsNotCached() {
    String currentUrl = getWebDriver().getCurrentUrl();
    if (!currentUrl.contains("timestamp=")) {
      navigateToAbsoluteUrl(currentUrl);
    }
  }

  static String makeUniqueUrl(String url, long unique) {
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
}
