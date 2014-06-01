package com.codeborne.selenide.impl;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.net.URL;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.WebDriverRunner.*;

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
    if (isIE() && !isLocalFile(url)) {
      url = makeUniqueUrlToAvoidIECaching(url, System.nanoTime());
    }

    try {
      WebDriver webdriver = getAndCheckWebDriver();
      webdriver.navigate().to(url);
      collectJavascriptErrors((JavascriptExecutor) webdriver);
    } catch (WebDriverException e) {
      e.addInfo("selenide.url", url);
      e.addInfo("selenide.baseUrl", baseUrl);
      throw e;
    }
  }

  protected void collectJavascriptErrors(JavascriptExecutor webdriver) {
    webdriver.executeScript(
        "window._selenide_jsErrors = [];\n" +
            "if (!window.onerror) {\n" +
            "  window.onerror = function (errorMessage, url, lineNumber) {\n" +
            "    var message = errorMessage + ' at ' + url + ':' + lineNumber;\n" +
            "    window._selenide_jsErrors.push(message);\n" +
            "    return false;\n" +
            "  };\n" +
            "}\n"
    );
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

  protected boolean isLocalFile(String url) {
    return url.startsWith("file:");
  }

  public void back() {
    getWebDriver().navigate().back();
  }

  public void forward() {
    getWebDriver().navigate().forward();
  }
}
