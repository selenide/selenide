package com.codeborne.selenide.impl;

import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Configuration.baseUrl;
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
        isLocalFile(relativeOrAbsoluteUrl)) {
      navigateToAbsoluteUrl(relativeOrAbsoluteUrl);
    } else {
      navigateToAbsoluteUrl(absoluteUrl(relativeOrAbsoluteUrl));
    }
  }

  public void open(URL url) {
    navigateToAbsoluteUrl(url.toExternalForm());
  }

  protected String absoluteUrl(String relativeUrl) {
    return baseUrl + relativeUrl;
  }

  protected void navigateToAbsoluteUrl(String url) {
    if (ie() && !isLocalFile(url)) {
      url = makeUniqueUrlToAvoidIECaching(url, System.nanoTime());
    }

    getWebDriver().navigate().to(url);
    waitUntilPageIsLoaded();
  }

  protected void waitUntilPageIsLoaded() {
    $(By.tagName("body")).should(appear);
  }

  protected String makeUniqueUrlToAvoidIECaching(String url, long unique) {
    if (url.contains("timestamp=")) {
      return url.replaceFirst("(.*)(timestamp=)(.*)([&#].*)", "$1$2" + unique + "$4")
          .replaceFirst("(.*)(timestamp=)(.*)$", "$1$2" + unique);
    } else {
      return url.contains("?") ?
          url + "&timestamp=" + unique:
          url + "?timestamp=" + unique;
    }
  }

  private boolean isLocalFile(String url) {
    return url.startsWith("file:");
  }
}
