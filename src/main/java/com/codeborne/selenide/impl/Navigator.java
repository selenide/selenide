package com.codeborne.selenide.impl;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.netty.handler.codec.http.HttpHeaders;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.net.URL;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.captureJavascriptErrors;
import static com.codeborne.selenide.WebDriverRunner.getAndCheckWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;

public class Navigator {
  private static final Logger log = Logger.getLogger(Navigator.class.getName());

  public void open(String relativeOrAbsoluteUrl) {
    open(relativeOrAbsoluteUrl, "", "", "");
  }

  public void open(URL url) {
    open(url, "", "", "");
  }

  public void open(String relativeOrAbsoluteUrl, String domain, String login, String password) {
    if (isAbsoluteUrl(relativeOrAbsoluteUrl)) {
      navigateToAbsoluteUrl(relativeOrAbsoluteUrl, domain, login, password);
    } else {
      navigateToAbsoluteUrl(absoluteUrl(relativeOrAbsoluteUrl), domain, login, password);
    }
  }

  public void open(URL url, String domain, String login, String password) {
    navigateToAbsoluteUrl(url.toExternalForm());
  }

  public void open(String relativeOrAbsoluteUrl, AuthenticationType authenticationType, String login, String password) {
    getAndCheckWebDriver();
    Credentials credentials = new Credentials(login, password);
    String authorization = String.format("%s %s", authenticationType.getValue(), credentials.encode());
    getSelenideProxy().addRequestFilter("headers.request", (request, contents, messageInfo) -> {
      final HttpHeaders headers = request.headers();
      headers.add("Authorization", authorization);
      headers.add("Proxy-Authorization", authorization);
      return null;
    });
    open(relativeOrAbsoluteUrl);
  }

  protected String absoluteUrl(String relativeUrl) {
    return baseUrl + relativeUrl;
  }

  protected void navigateToAbsoluteUrl(String url) {
    navigateToAbsoluteUrl(url, "", "", "");
  }

  protected void navigateToAbsoluteUrl(String url, String domain, String login, String password) {
    if (isIE() && !isLocalFile(url)) {
      url = makeUniqueUrlToAvoidIECaching(url, System.nanoTime());
    }
    else {
      if (!domain.isEmpty()) domain += "%5C";
      if (!login.isEmpty()) login += ":";
      if (!password.isEmpty()) password += "@";
      int idx1 = url.indexOf("://") + 3;
      url = (idx1 < 3 ? "" : (url.substring(0, idx1 - 3) + "://"))
              + domain
              + login
              + password
              + (idx1 < 3 ? url : url.substring(idx1));
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
    if (!captureJavascriptErrors) return;

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

  boolean isAbsoluteUrl(String relativeOrAbsoluteUrl) {
    return relativeOrAbsoluteUrl.toLowerCase().startsWith("http:") ||
        relativeOrAbsoluteUrl.toLowerCase().startsWith("https:") ||
        isLocalFile(relativeOrAbsoluteUrl);
  }

  protected boolean isLocalFile(String url) {
    return url.toLowerCase().startsWith("file:");
  }

  public void back() {
    getWebDriver().navigate().back();
  }

  public void forward() {
    getWebDriver().navigate().forward();
  }

  public void refresh() {
    getWebDriver().navigate().refresh();
  }
}
