package integration;

import com.automation.remarks.junit5.VideoExtension;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.codeborne.selenide.junit5.TextReportExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Browsers.*;
import static com.codeborne.selenide.Configuration.browserSize;
import static com.codeborne.selenide.Configuration.clickViaJs;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Configuration.versatileSetValue;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static com.codeborne.selenide.WebDriverRunner.isSafari;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@ExtendWith({ScreenShooterExtension.class, TextReportExtension.class, VideoExtension.class})
public abstract class IntegrationTest extends BaseIntegrationTest {
  private long defaultTimeout;

  @BeforeAll
  static void resetSettingsBeforeClass() {
    resetSettings();
  }

  @BeforeEach
  final void setUpEach() {
    resetSettings();
    restartReallyUnstableBrowsers();
    rememberTimeout();
  }

  @AfterEach
  public void restoreDefaultProperties() {
    timeout = defaultTimeout;
    clickViaJs = false;
  }

  @AfterAll
  public static void restartUnstableWebdriver() {
    if (isIE() || isPhantomjs()) {
      closeWebDriver();
    }
  }

  private static void resetSettings() {
    Configuration.browser = System.getProperty("selenide.browser", CHROME);
    Configuration.baseUrl = getBaseUrl();
    Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));
    Configuration.reportsFolder = System.getProperty("selenide.reportsFolder", "build/reports/tests");
    fastSetValue = false;
    versatileSetValue = false;
    browserSize = System.getProperty("selenide.browserSize", "1200x960");
    Configuration.proxyPort = 0;
    Configuration.proxyHost = "";
    useProxy(!isPhantomjs());
  }

  private void restartReallyUnstableBrowsers() {
    if (isSafari()) {
      closeWebDriver();
    }
  }

  private void rememberTimeout() {
    defaultTimeout = timeout;
  }

  protected void openFile(String fileName) {
    open("/" + fileName + "?browser=" + browser +
      "&timeout=" + timeout);
  }

  <T> T openFile(String fileName, Class<T> pageObjectClass) {
    return open("/" + fileName + "?browser=" + browser +
      "&timeout=" + timeout, pageObjectClass);
  }

  /**
   * Turns proxy on / off
   * When toggling (on <-> off) happens, browser is closed
   * @param proxyEnabled true - turn on, false - turn off
   */
  protected static void useProxy(boolean proxyEnabled) {
    if (proxyEnabled) {
      assumeFalse(isPhantomjs()); // I don't know why, but PhantomJS seems to ignore proxy
    }

    if (Configuration.proxyEnabled != proxyEnabled) {
      Selenide.close();
    }
    Configuration.proxyEnabled = proxyEnabled;
    Configuration.fileDownload = proxyEnabled ? PROXY : HTTPGET;
  }

  protected void givenHtml(String... html) {
    open("/empty.html");
    executeJavaScript(
      "document.querySelector('body').innerHTML = arguments[0];",
      String.join(" ", html)
    );
  }
}
