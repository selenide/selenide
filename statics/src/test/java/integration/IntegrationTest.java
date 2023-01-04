package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.opentest4j.TestAbortedException;

import java.awt.*;
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
      open("about:blank");
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
    open("/" + fileName + "?browser=" + browser +
      "&timeout=" + timeout);
  }

  protected <T> T openFile(String fileName, Class<T> pageObjectClass) {
    return open("/" + fileName + "?browser=" + browser +
      "&timeout=" + timeout, pageObjectClass);
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

  protected static <T extends MutableCapabilities> T addSslErrorIgnoreCapabilities(T options) {
    addSslErrorIgnoreOptions(options);
    return options;
  }

  protected static ChromeOptions addHeadless(ChromeOptions options) {
    if (Configuration.headless) options.setHeadless(true);
    return options;
  }

  protected static FirefoxOptions addHeadless(FirefoxOptions options) {
    if (Configuration.headless) options.setHeadless(true);
    return options;
  }

  private static void addSslErrorIgnoreOptions(MutableCapabilities options) {
    options.setCapability(ACCEPT_INSECURE_CERTS, true);
  }

  protected void assumeClipboardSupported() {
    assumeThat(GraphicsEnvironment.isHeadless()).isFalse();
    try {
      Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
    } catch (UnsupportedFlavorException | IOException e) {
      throw new TestAbortedException("Clipboard not supported in current environment", e);
    }
  }
}
