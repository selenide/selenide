package com.codeborne.selenide.impl;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.proxy.AuthenticationFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.net.URL;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;

public class Navigator {
  private static final Logger log = Logger.getLogger(Navigator.class.getName());

  private JavascriptErrorsCollector javascriptErrorsCollector = new JavascriptErrorsCollector();
  private BasicAuthUrl basicAuthUrl = new BasicAuthUrl();

  public void open(SelenideDriver driver, String relativeOrAbsoluteUrl) {
    navigateTo(driver, relativeOrAbsoluteUrl, AuthenticationType.BASIC, "", "", "");
  }

  public void open(SelenideDriver driver, URL url) {
    navigateTo(driver, url.toExternalForm(), AuthenticationType.BASIC, "", "", "");
  }

  public void open(SelenideDriver driver, String relativeOrAbsoluteUrl, String domain, String login, String password) {
    navigateTo(driver, relativeOrAbsoluteUrl, AuthenticationType.BASIC, domain, login, password);
  }

  public void open(SelenideDriver driver, URL url, String domain, String login, String password) {
    navigateTo(driver, url.toExternalForm(), AuthenticationType.BASIC, domain, login, password);
  }

  public void open(SelenideDriver driver, String relativeOrAbsoluteUrl,
                   AuthenticationType authenticationType, Credentials credentials) {
    navigateTo(driver, relativeOrAbsoluteUrl, authenticationType, "", credentials.login, credentials.password);
  }

  private AuthenticationFilter basicAuthRequestFilter(SelenideProxyServer selenideProxy) {
    return selenideProxy.requestFilter("authentication");
  }

  String absoluteUrl(String relativeOrAbsoluteUrl) {
    return isAbsoluteUrl(relativeOrAbsoluteUrl) ? relativeOrAbsoluteUrl : baseUrl + relativeOrAbsoluteUrl;
  }

  private void navigateTo(SelenideDriver driver, String url,
                          AuthenticationType authenticationType, String domain, String login, String password) {
    forceProxyIfNeeded();

    url = absoluteUrl(url);
    url = appendBasicAuthIfNeeded(url, authenticationType, domain, login, password);

    SelenideLog log = SelenideLogger.beginStep("open", url);
    try {
      WebDriver webDriver = driver.getAndCheckWebDriver();
      beforeNavigateTo(driver.getProxyServer(), authenticationType, domain, login, password);
      webDriver.navigate().to(url);
      javascriptErrorsCollector.collectJavascriptErrors(driver.getWebDriver());
      SelenideLogger.commitStep(log, PASS);
    }
    catch (WebDriverException e) {
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

  private void forceProxyIfNeeded() {
    if (!Configuration.proxyEnabled && Configuration.fileDownload == PROXY) {
      log.warning("Configuration.proxyEnabled == false but Configuration.fileDownload == PROXY. " +
        "We will enable proxy server automatically.");
      Configuration.proxyEnabled = true;
    }
  }

  private void beforeNavigateTo(SelenideProxyServer selenideProxy,
                                AuthenticationType authenticationType, String domain, String login, String password) {
    if (Configuration.proxyEnabled) {
      beforeNavigateToWithProxy(selenideProxy, authenticationType, domain, login, password);
    }
    else {
      beforeNavigateToWithoutProxy(authenticationType, domain, login, password);
    }
  }

  private void beforeNavigateToWithProxy(SelenideProxyServer selenideProxy,
                                         AuthenticationType authenticationType, String domain, String login, String password) {
    if (hasAuthentication(domain, login, password)) {
      basicAuthRequestFilter(selenideProxy).setAuthentication(authenticationType, new Credentials(login, password));
    }
    else {
      basicAuthRequestFilter(selenideProxy).removeAuthentication();
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
      !Configuration.proxyEnabled &&
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

  public void back(SelenideDriver driver) {
    driver.getWebDriver().navigate().back();
  }

  public void forward(SelenideDriver driver) {
    driver.getWebDriver().navigate().forward();
  }

  public void refresh(SelenideDriver driver) {
    driver.getWebDriver().navigate().refresh();
  }
}
