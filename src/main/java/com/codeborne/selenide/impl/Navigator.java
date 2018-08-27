package com.codeborne.selenide.impl;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.proxy.AuthenticationFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.net.URL;

import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.WebDriverRunner.getAndCheckWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;

public class Navigator {
  private JavascriptErrorsCollector javascriptErrorsCollector = new JavascriptErrorsCollector();

  public void open(String relativeOrAbsoluteUrl) {
    navigateTo(relativeOrAbsoluteUrl, AuthenticationType.BASIC, "", "", "");
  }

  public void open(URL url) {
    navigateTo(url.toExternalForm(), AuthenticationType.BASIC, "", "", "");
  }

  public void open(String relativeOrAbsoluteUrl, String domain, String login, String password) {
    navigateTo(relativeOrAbsoluteUrl, AuthenticationType.BASIC, domain, login, password);
  }

  public void open(URL url, String domain, String login, String password) {
    navigateTo(url.toExternalForm(), AuthenticationType.BASIC, domain, login, password);
  }

  public void open(String relativeOrAbsoluteUrl, AuthenticationType authenticationType, Credentials credentials) {
    navigateTo(relativeOrAbsoluteUrl, authenticationType, "", credentials.login, credentials.password);
  }

  private AuthenticationFilter basicAuthRequestFilter() {
    getAndCheckWebDriver();
    SelenideProxyServer selenideProxy = getSelenideProxy();
    return selenideProxy.requestFilter("authentication");
  }

  String absoluteUrl(String relativeOrAbsoluteUrl) {
    return isAbsoluteUrl(relativeOrAbsoluteUrl) ? relativeOrAbsoluteUrl : baseUrl + relativeOrAbsoluteUrl;
  }

  private void navigateTo(String url, AuthenticationType authenticationType, String domain, String login, String password) {
    url = absoluteUrl(url);

    if (isIE() && !isLocalFile(url)) {
      url = makeUniqueUrlToAvoidIECaching(url, System.nanoTime());
    }

    boolean hasAuthentication = !domain.isEmpty() || !login.isEmpty() || !password.isEmpty();
    if (hasAuthentication) {
      if (Configuration.fileDownload == PROXY) {
        basicAuthRequestFilter().setAuthentication(authenticationType, new Credentials(login, password));
      }
      else if (authenticationType == AuthenticationType.BASIC) {
        url = appendBasicAuthToURL(url, domain, login, password);
      }
      else {
        throw new UnsupportedOperationException("Cannot use " + authenticationType + " authentication without proxy server");
      }
    }
    else {
      if (Configuration.fileDownload == PROXY) {
        basicAuthRequestFilter().removeAuthentication();
      }
    }

    SelenideLog log = SelenideLogger.beginStep("open", url);
    try {
      WebDriver webdriver = getAndCheckWebDriver();
      webdriver.navigate().to(url);
      javascriptErrorsCollector.collectJavascriptErrors(webdriver);
      SelenideLogger.commitStep(log, PASS);
    } catch (WebDriverException e) {
      SelenideLogger.commitStep(log, e);
      e.addInfo("selenide.url", url);
      e.addInfo("selenide.baseUrl", baseUrl);
      throw e;
    }
    catch (RuntimeException | Error e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
  }

  String appendBasicAuthToURL(String url, String domain, String login, String password) {
    if (!domain.isEmpty()) domain += "%5C";
    if (!login.isEmpty()) login += ":";
    if (!password.isEmpty()) password += "@";
    int index = url.indexOf("://") + 3;
    if (index < 3) return domain + login + password + url;

    return url.substring(0, index - 3) + "://"
      + domain
      + login
      + password
      + url.substring(index);
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

  private boolean isLocalFile(String url) {
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
