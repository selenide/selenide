package com.codeborne.selenide.impl;

import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.net.URL;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;

public class Navigator {
  private static final Logger log = Logger.getLogger(Navigator.class.getName());

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

    SelenideLog log = SelenideLogger.beginStep("open", url);
    try {
      WebDriver webdriver = getAndCheckWebDriver();
      webdriver.navigate().to(url);
      collectJavascriptErrors((JavascriptExecutor) webdriver);
      SelenideLogger.commitStep(log, PASS);
    } catch (WebDriverException e) {
      SelenideLogger.commitStep(log, e);
      e.addInfo("selenide.url", url);
      e.addInfo("selenide.baseUrl", baseUrl);
      throw e;
    }
    catch (RuntimeException e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
    catch (Error e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
  }

  protected void collectJavascriptErrors(JavascriptExecutor webdriver) {
    try {
      webdriver.executeScript(
          "if (!window._selenide_jsErrors) {\n" +
              "  window._selenide_jsErrors = [];\n" +
              "}\n" +
              "if (!window.onerror) {\n" +
              "  window.onerror = function (errorMessage, url, lineNumber) {\n" +
              "    var message = errorMessage + ' at ' + url + ':' + lineNumber;\n" +
              "    window._selenide_jsErrors.push(message);\n" +
              "    return false;\n" +
              "  };\n" +
              "}\n"
      );
    } catch (UnsupportedOperationException cannotExecuteJsAgainstPlainTextPage) {
      log.warning(cannotExecuteJsAgainstPlainTextPage.toString());
    } catch (WebDriverException cannotExecuteJs) {
      log.severe(cannotExecuteJs.toString());
    }
  }

  protected String makeUniqueUrlToAvoidIECaching(String url, long unique) {
    if (url.contains("timestamp=")) {
      return url.replaceFirst("(.*)(timestamp=)(.*)([&#].*)", "$1$2" + unique + "$4")
          .replaceFirst("(.*)(timestamp=)(.*)$", "$1$2" + unique);
    } else {
      return url.contains("?") ?
          url + "&timestamp=" + unique :
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
