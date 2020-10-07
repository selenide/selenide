package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.SelenideConfig;
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

import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

final class NavigatorTest implements WithAssertions {
  private final Navigator navigator = new Navigator();
  private final SelenideDriver selenideDriver = mock(SelenideDriver.class);
  private final WebDriver driver = mock(WebDriver.class);
  private final WebDriver.Navigation navigation = mock(WebDriver.Navigation.class);
  private final SelenideProxyServer selenideProxy = mock(SelenideProxyServer.class);
  private final AuthenticationFilter authenticationFilter = mock(AuthenticationFilter.class);
  private final SelenideConfig config = new SelenideConfig().fileDownload(HTTPGET);

  @BeforeEach
  void setUp() {
    doReturn(config).when(selenideDriver).config();
    doReturn(driver).when(selenideDriver).getAndCheckWebDriver();
    doReturn(selenideProxy).when(selenideDriver).getProxy();
    doReturn(navigation).when(driver).navigate();
    doReturn(true).when(selenideProxy).isStarted();
    doReturn(authenticationFilter).when(selenideProxy).requestFilter("authentication");
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
    assertThat(navigator.isAbsoluteUrl("https://selenide.org\n"))
      .as("ignores newline")
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
    assertThat(navigator.isAbsoluteUrl("about:blank"))
      .as("case insensitive: FILE")
      .isTrue();

    assertThat(navigator.isAbsoluteUrl("/tmp/memory.dump"))
      .as("relative url")
      .isFalse();
    assertThat(navigator.isAbsoluteUrl("/payments/history"))
      .as("relative url")
      .isFalse();
    assertThat(navigator.isAbsoluteUrl("/tmp/memory.dump?url=http://selenide.org"))
      .as("relative url")
      .isFalse();
  }

  @Test
  void returnsAbsoluteUrl() {
    config.baseUrl("http://localhost:8080");
    assertThat(navigator.absoluteUrl(config, "/users/id=1")).isEqualTo("http://localhost:8080/users/id=1");
    assertThat(navigator.absoluteUrl(config, "http://host:port/users/id=1")).isEqualTo("http://host:port/users/id=1");
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
    Mockito.verify(listener).afterEvent(ArgumentMatchers.argThat(log ->
      "open".equals(log.getElement()) && "https://some.com/login".equals(log.getSubject())));
  }

  @Test
  void open_withoutAuthentication_resetsPreviousAuthentication() {
    config.browser("opera");
    config.proxyEnabled(true);

    navigator.open(selenideDriver, "https://some.com/login");

    Mockito.verify(navigation).to("https://some.com/login");
    Mockito.verify(authenticationFilter).removeAuthentication();
  }

  @Test
  void open_withBasicAuth_noProxy() {
    config.browser("opera");
    config.proxyEnabled(false);

    navigator.open(selenideDriver, "https://some.com/login", "", "basic-auth-login", "basic-auth-password");

    Mockito.verify(navigation).to("https://basic-auth-login:basic-auth-password@some.com/login");
  }

  @Test
  void open_withBasicAuth_withProxy() {
    config.browser("opera");
    config.proxyEnabled(true);

    navigator.open(selenideDriver, "https://some.com/login", "", "basic-auth-login", "basic-auth-password");

    Mockito.verify(navigation).to("https://some.com/login");
    Mockito.verify(authenticationFilter)
      .setAuthentication(eq(AuthenticationType.BASIC), refEq(new Credentials("basic-auth-login", "basic-auth-password")));
  }

  @Test
  void startsProxyServer_evenIfProxyIsNotEnabled_butFileDownloadModeIsProxy() {
    config.proxyEnabled(false);
    config.fileDownload(PROXY);

    assertThatThrownBy(() -> navigator.open(selenideDriver, "https://some.com/login"))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("config.proxyEnabled == false but config.fileDownload == PROXY. You need to enable proxy server automatically.");
  }
}
