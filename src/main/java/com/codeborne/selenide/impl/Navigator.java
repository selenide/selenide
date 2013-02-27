package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.ie;

public class Navigator {
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
  public void open(String relativeOrAbsoluteUrl) {
    if (relativeOrAbsoluteUrl.startsWith("http:") ||
        relativeOrAbsoluteUrl.startsWith("https:") ||
        relativeOrAbsoluteUrl.startsWith("file:")) {
      navigateToAbsoluteUrl(relativeOrAbsoluteUrl);
    } else {
      navigateToAbsoluteUrl(absoluteUrl(relativeOrAbsoluteUrl));
    }
  }

  public void open(URL url) {
    navigateToAbsoluteUrl(url.toExternalForm());
  }

  protected String absoluteUrl(String relativeUrl) {
    return Configuration.getBaseUrl() + relativeUrl;
  }

  protected void navigateToAbsoluteUrl(String url) {
    if (ie()) {
      getWebDriver().navigate().to(makeUniqueUrlToAvoidIECaching(url, System.nanoTime()));
      waitUntilPageIsLoaded();
      toBeSureThatPageIsNotCached();
    }
    else {
      getWebDriver().navigate().to(url);
      waitUntilPageIsLoaded();
    }
  }

  protected void waitUntilPageIsLoaded() {
    $(By.tagName("body")).should(appear);
  }

  protected void toBeSureThatPageIsNotCached() {
    String currentUrl = getWebDriver().getCurrentUrl();
    if (!currentUrl.contains("timestamp=")) {
      navigateToAbsoluteUrl(currentUrl);
    }
  }

  protected String makeUniqueUrlToAvoidIECaching(String url, long unique) {
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
