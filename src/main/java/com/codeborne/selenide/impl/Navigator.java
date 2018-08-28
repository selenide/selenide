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
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;

public class Navigator {
  private JavascriptErrorsCollector javascriptErrorsCollector = new JavascriptErrorsCollector();
  private BasicAuthUrl basicAuthUrl = new BasicAuthUrl();

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
    SelenideProxyServer selenideProxy = getSelenideProxy();
    return selenideProxy.requestFilter("authentication");
  }

  String absoluteUrl(String relativeOrAbsoluteUrl) {
    return isAbsoluteUrl(relativeOrAbsoluteUrl) ? relativeOrAbsoluteUrl : baseUrl + relativeOrAbsoluteUrl;
  }

  private void navigateTo(String url, AuthenticationType authenticationType, String domain, String login, String password) {
    url = absoluteUrl(url);
    url = appendBasicAuthIfNeeded(url, authenticationType, domain, login, password);

    SelenideLog log = SelenideLogger.beginStep("open", url);
    try {
      WebDriver webdriver = getAndCheckWebDriver();
      beforeNavigateTo(authenticationType, domain, login, password);
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

  private void beforeNavigateTo(AuthenticationType authenticationType, String domain, String login, String password) {
    if (Configuration.fileDownload == PROXY) {
      beforeNavigateToWithProxy(authenticationType, domain, login, password);
    }
    else {
      beforeNavigateToWithoutProxy(authenticationType, domain, login, password);
    }
  }

  private void beforeNavigateToWithProxy(AuthenticationType authenticationType, String domain, String login, String password) {
    if (hasAuthentication(domain, login, password)) {
      basicAuthRequestFilter().setAuthentication(authenticationType, new Credentials(login, password));
    }
    else {
      basicAuthRequestFilter().removeAuthentication();
    }
  }

  private void beforeNavigateToWithoutProxy(AuthenticationType authenticationType, String domain, String login, String password) {
    if (hasAuthentication(domain, login, password) && authenticationType != AuthenticationType.BASIC) {
      throw new UnsupportedOperationException("Cannot use " + authenticationType + " authentication without proxy server");
    }
  }

  private boolean hasAuthentication(String domain, String login, String password) {
    return !domain.isEmpty() || !login.isEmpty() || !password.isEmpty();
  }

  private String appendBasicAuthIfNeeded(String url, AuthenticationType authType, String domain, String login, String password) {
    return passBasicAuthThroughUrl(authType, domain, login, password)
      ? basicAuthUrl.appendBasicAuthToURL(url, domain, login, password)
      : url;
  }

  private boolean passBasicAuthThroughUrl(AuthenticationType authenticationType, String domain, String login, String password) {
    return hasAuthentication(domain, login, password) &&
      Configuration.fileDownload != PROXY &&
      authenticationType == AuthenticationType.BASIC;
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
