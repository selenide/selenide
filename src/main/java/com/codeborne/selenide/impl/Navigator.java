package com.codeborne.selenide.impl;

import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.ie;

public class Navigator {
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
