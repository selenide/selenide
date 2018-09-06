package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.proxy.AuthenticationFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;

class NavigatorTest implements WithAssertions {
  private Navigator navigator = new Navigator();
  SelenideDriver selenideDriver = mock(SelenideDriver.class);
  WebDriver driver = mock(WebDriver.class);
  WebDriver.Navigation navigation = mock(WebDriver.Navigation.class);
  SelenideProxyServer selenideProxy = mock(SelenideProxyServer.class);
  AuthenticationFilter authenticationFilter = mock(AuthenticationFilter.class);

  @BeforeEach
  void setUp() {
    Configuration.fileDownload = HTTPGET;
    Mockito.doReturn(driver).when(selenideDriver).getAndCheckWebDriver();
    Mockito.doReturn(selenideProxy).when(selenideDriver).getProxy();
    Mockito.doReturn(navigation).when(driver).navigate();
    Mockito.doReturn(authenticationFilter).when(selenideProxy).requestFilter("authentication");
  }

  @Test
  void detectsAbsoluteUrls() {
    assertThat(navigator.isAbsoluteUrl("http://selenide.org"))
      .as("protocol http")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("https://selenide.org"))
      .as("protocol https")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("file:///tmp/memory.dump"))
      .as("protocol file")
      .isTrue();

    assertThat(navigator.isAbsoluteUrl("HTTP://SELENIDE.ORG"))
      .as("case insensitive: HTTP")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("HTTPS://SELENIDE.ORG"))
      .as("case insensitive: HTTPS")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("FILE:///TMP/MEMORY.DUMP"))
      .as("case insensitive: FILE")
      .isTrue();

    assertThat(navigator.isAbsoluteUrl("/tmp/memory.dump"))
      .as("relative url")
      .isFalse();
    assertThat(navigator.isAbsoluteUrl("/payments/history"))
      .as("relative url")
      .isFalse();
  }

  @Test
  void returnsAbsoluteUrl() {
    Configuration.baseUrl = "http://localhost:8080";
    assertThat(navigator.absoluteUrl("/users/id=1")).isEqualTo("http://localhost:8080/users/id=1");
    assertThat(navigator.absoluteUrl("http://host:port/users/id=1")).isEqualTo("http://host:port/users/id=1");
  }

  @Test
  void open_withoutAuthentication() {
    navigator.open(selenideDriver, "https://some.com/login");

    Mockito.verify(navigation).to("https://some.com/login");
  }

  @Test
  void open_addsEventToLog() {
    LogEventListener listener = mock(LogEventListener.class);
    SelenideLogger.addListener("listener-01", listener);

    navigator.open(selenideDriver, "https://some.com/login");

    Mockito.verify(navigation).to("https://some.com/login");
    Mockito.verify(listener).onEvent(ArgumentMatchers.argThat(log ->
      "open".equals(log.getElement()) && "https://some.com/login".equals(log.getSubject())));
  }

  @Test
  void open_withoutAuthentication_resetsPreviousAuthentication() {
    Configuration.browser = "opera";
    Configuration.proxyEnabled = true;

    navigator.open(selenideDriver, "https://some.com/login");

    Mockito.verify(navigation).to("https://some.com/login");
    Mockito.verify(authenticationFilter).removeAuthentication();
  }

  @Test
  void open_withBasicAuth_noProxy() {
    Configuration.browser = "opera";
    Configuration.proxyEnabled = false;

    navigator.open(selenideDriver, "https://some.com/login", "", "basic-auth-login", "basic-auth-password");

    Mockito.verify(navigation).to("https://basic-auth-login:basic-auth-password@some.com/login");
  }

  @Test
  void open_withBasicAuth_withProxy() {
    Configuration.browser = "opera";
    Configuration.proxyEnabled = true;

    navigator.open(selenideDriver, "https://some.com/login", "", "basic-auth-login", "basic-auth-password");

    Mockito.verify(navigation).to("https://some.com/login");
    Mockito.verify(authenticationFilter)
      .setAuthentication(eq(AuthenticationType.BASIC), refEq(new Credentials("basic-auth-login", "basic-auth-password")));
  }

  @Test
  void startsProxyServer_evenIfProxyIsNotEnabled_butFileDownloadModeIsProxy() {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = PROXY;

    navigator.open(selenideDriver, "https://some.com/login");

    assertThat(Configuration.proxyEnabled).isTrue();
  }
}
