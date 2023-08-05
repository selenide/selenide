package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.BasicAuthCredentials;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.proxy.AuthenticationFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URL;
import java.util.regex.Pattern;

import static com.codeborne.selenide.AuthenticationType.BASIC;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.drivercommands.BasicAuthUtils.appendBasicAuthToURL;
import static com.codeborne.selenide.drivercommands.BasicAuthUtils.registerBasicAuth;
import static java.util.regex.Pattern.DOTALL;

@ParametersAreNonnullByDefault
public class Navigator {
  private static final Pattern ABSOLUTE_URL_REGEX = Pattern.compile("^[a-zA-Z-]+:.*", DOTALL);

  public void open(SelenideDriver driver, String relativeOrAbsoluteUrl) {
    navigateTo(driver, relativeOrAbsoluteUrl, null, null);
  }

  public void open(SelenideDriver driver, URL url) {
    navigateTo(driver, url.toExternalForm(), null, null);
  }

  public void open(SelenideDriver driver, String relativeOrAbsoluteUrl, String domain, String login, String password) {
    navigateTo(driver, relativeOrAbsoluteUrl, BASIC, new BasicAuthCredentials(domain, login, password));
  }

  public void open(SelenideDriver driver, URL url, String domain, String login, String password) {
    navigateTo(driver, url.toExternalForm(), BASIC, new BasicAuthCredentials(domain, login, password));
  }

  public void open(SelenideDriver driver, String relativeOrAbsoluteUrl,
                   AuthenticationType authenticationType, Credentials credentials) {
    navigateTo(driver, relativeOrAbsoluteUrl, authenticationType, credentials);
  }

  private AuthenticationFilter basicAuthRequestFilter(SelenideProxyServer selenideProxy) {
    return selenideProxy.requestFilter("authentication");
  }

  @Nonnull
  String absoluteUrl(Config config, String relativeOrAbsoluteUrl) {
    return isAbsoluteUrl(relativeOrAbsoluteUrl) ? relativeOrAbsoluteUrl : config.baseUrl() + relativeOrAbsoluteUrl;
  }

  private void navigateTo(SelenideDriver driver,
                          String relativeOrAbsoluteUrl,
                          @Nullable AuthenticationType authenticationType,
                          @Nullable Credentials credentials) {
    checkThatProxyIsEnabled(driver.config());

    String absoluteUrl = absoluteUrl(driver.config(), relativeOrAbsoluteUrl);

    SelenideLogger.run("open", absoluteUrl, () -> {
      try {
        WebDriver webDriver = driver.getAndCheckWebDriver();
        String url = applyBasicAuthIfNeeded(driver.config(), absoluteUrl, webDriver, authenticationType, credentials);
        beforeNavigateTo(driver, authenticationType, credentials);
        webDriver.navigate().to(url);
      }
      catch (WebDriverException e) {
        e.addInfo("selenide.url", absoluteUrl);
        e.addInfo("selenide.baseUrl", driver.config().baseUrl());
        if (driver.config().remote() != null) {
          e.addInfo("selenide.remote", driver.config().remote());
        }
        throw e;
      }
    });
  }

  public void open(SelenideDriver driver) {
    checkThatProxyIsEnabled(driver.config());
    SelenideLogger.run("open", "", driver::getAndCheckWebDriver);
  }

  private void checkThatProxyIsEnabled(Config config) {
    if (!config.proxyEnabled() && config.fileDownload() == PROXY) {
      throw new IllegalStateException("config.proxyEnabled == false but config.fileDownload == PROXY. " +
        "You need to enable proxy server automatically.");
    }
  }

  private void beforeNavigateTo(SelenideDriver driver,
                                @Nullable AuthenticationType authenticationType,
                                @Nullable Credentials credentials) {
    Config config = driver.config();
    if (config.proxyEnabled()) {
      SelenideProxyServer selenideProxy = driver.getProxy();
      beforeNavigateToWithProxy(selenideProxy, authenticationType, credentials);
    }
    else {
      beforeNavigateToWithoutProxy(authenticationType, credentials);
    }
  }

  private void beforeNavigateToWithProxy(SelenideProxyServer selenideProxy,
                                         @Nullable AuthenticationType authenticationType, @Nullable Credentials credentials) {
    if (authenticationType != null && credentials != null) {
      basicAuthRequestFilter(selenideProxy).setAuthentication(authenticationType, credentials);
    }
    else {
      basicAuthRequestFilter(selenideProxy).removeAuthentication();
    }
  }

  private void beforeNavigateToWithoutProxy(@Nullable AuthenticationType authenticationType, @Nullable Credentials credentials) {
    if (credentials != null && authenticationType != BASIC) {
      throw new UnsupportedOperationException("Cannot use " + authenticationType + " authentication without proxy server");
    }
  }

  private String applyBasicAuthIfNeeded(Config config,
                                        String url,
                                        WebDriver webDriver,
                                        @Nullable AuthenticationType authType,
                                        @Nullable Credentials credentials) {
    if (registerBasicAuth(webDriver, credentials)) {
      return url;
    }
    return passBasicAuthThroughUrl(config, authType, credentials)
      ? appendBasicAuthToURL(url, (BasicAuthCredentials) credentials)
      : url;
  }

  private boolean passBasicAuthThroughUrl(Config config,
                                          @Nullable AuthenticationType authenticationType,
                                          @Nullable Credentials credentials) {
    return authenticationType == BASIC && credentials != null && !config.proxyEnabled();
  }

  boolean isAbsoluteUrl(String relativeOrAbsoluteUrl) {
    return ABSOLUTE_URL_REGEX.matcher(relativeOrAbsoluteUrl).matches();
  }

  public void back(Driver driver) {
    SelenideLogger.run("back", "", () -> {
      driver.getWebDriver().navigate().back();
    });
  }

  public void forward(Driver driver) {
    SelenideLogger.run("forward", "", () -> {
      driver.getWebDriver().navigate().forward();
    });
  }

  public void refresh(Driver driver) {
    SelenideLogger.run("refresh", "", () -> {
      driver.getWebDriver().navigate().refresh();
    });
  }
}
