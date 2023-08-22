package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.opentest4j.TestAbortedException;

import javax.annotation.Nullable;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static com.codeborne.selenide.AssertionMode.STRICT;
import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.TextCheck.FULL_TEXT;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;

@ExtendWith(ScreenShooterExtension.class)
public abstract class IntegrationTest extends BaseIntegrationTest {
  @BeforeAll
  static void resetSettingsBeforeClass() {
    resetSettings();
  }

  @BeforeEach
  @AfterEach
  void setUpEach() {
    resetSettings();
    turnProxy(false);
  }

  @BeforeEach
  final void openBlankPage() {
    if (hasWebDriverStarted()) {
      retry(() -> open("about:blank"), 5);
    }
  }

  @AfterAll
  public static void restartUnstableWebdriver() {
    if (isIE()) {
      closeWebDriver();
    }
  }

  protected static void resetSettings() {
    timeout = 1;
    Configuration.pollingInterval = 0;
    Configuration.clickViaJs = false;
    Configuration.downloadsFolder = "build/downloads";
    Configuration.pageLoadTimeout = Long.parseLong(System.getProperty("selenide.pageLoadTimeout", "30000"));
    Configuration.browser = System.getProperty("selenide.browser", CHROME);
    Configuration.baseUrl = getBaseUrl();
    Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));
    Configuration.reportsFolder = System.getProperty("selenide.reportsFolder", "build/reports/tests");
    Configuration.fastSetValue = false;
    Configuration.browserSize = System.getProperty("selenide.browserSize", "1200x960");
    Configuration.assertionMode = STRICT;
    Configuration.proxyPort = 0;
    Configuration.proxyHost = "";
    Configuration.fileDownload = HTTPGET;
    Configuration.reopenBrowserOnFail = Boolean.parseBoolean(System.getProperty("selenide.reopenBrowserOnFail", "false"));
    Configuration.textCheck = FULL_TEXT;
    Configuration.browserCapabilities = new MutableCapabilities();
  }

  protected void openFile(String fileName) {
    retry(() -> open("/" + fileName), 5);
  }

  protected <T> T openFile(String fileName, Class<T> pageObjectClass) {
    retry(() -> open("/" + fileName), 5);
    return page(pageObjectClass);
  }

  /**
   * Turns proxy on / off
   * When toggling (on <-> off) happens, browser is closed
   * @param proxyEnabled true - turn on, false - turn off
   */
  protected static void turnProxy(boolean proxyEnabled) {
    if (Configuration.proxyEnabled != proxyEnabled) {
      Selenide.closeWebDriver();
    }
    Configuration.proxyEnabled = proxyEnabled;
    Configuration.fileDownload = proxyEnabled ? PROXY : HTTPGET;
  }

  protected static void useProxy(boolean proxyEnabled) {
    if (Configuration.proxyEnabled != proxyEnabled) {
      throw new IllegalStateException("Expected proxy mode: " + proxyEnabled + ", actual: " + Configuration.proxyEnabled);
    }
  }

  protected void givenHtml(String... html) {
    open("/empty.html");
    executeJavaScript(
      "document.querySelector('body').innerHTML = arguments[0];",
      String.join(" ", html)
    );
  }

  protected static ChromeDriver openChrome() {
    return openChrome(null);
  }

  protected static ChromeDriver openChrome(@Nullable SelenideProxyServer proxy) {
    return new ChromeDriver(chromeOptions(proxy == null ? null : proxy.getSeleniumProxy()));
  }

  protected static ChromeOptions chromeOptions(@Nullable Proxy proxy) {
    ChromeOptions options = new ChromeOptions();
    if (Configuration.headless) {
      options.addArguments("--headless=new");
    }
    if (proxy != null) {
      options.setProxy(proxy);
    }
    options.addArguments("--proxy-bypass-list=<-loopback>");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--no-sandbox");
    options.setCapability(ACCEPT_INSECURE_CERTS, true);
    return options;
  }

  protected static FirefoxDriver openFirefox() {
    return openFirefox(null);
  }

  protected static FirefoxDriver openFirefox(@Nullable SelenideProxyServer proxy) {
    return new FirefoxDriver(firefoxOptions(proxy));
  }

  protected static FirefoxOptions firefoxOptions(@Nullable SelenideProxyServer proxy) {
    FirefoxOptions options = new FirefoxOptions();
    if (Configuration.headless) {
      options.addArguments("-headless");
    }
    if (proxy != null) {
      options.setProxy(proxy.getSeleniumProxy());
    }
    options.addPreference("network.proxy.no_proxies_on", "");
    options.addPreference("network.proxy.allow_hijacking_localhost", true);
    options.setCapability(ACCEPT_INSECURE_CERTS, true);
    return options;
  }

  protected void assumeClipboardSupported() {
    assumeThat(headless).isFalse();
    assumeThat(GraphicsEnvironment.isHeadless()).isFalse();
    try {
      Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
    } catch (UnsupportedFlavorException | IOException e) {
      throw new TestAbortedException("Clipboard not supported in current environment", e);
    }
  }
}
