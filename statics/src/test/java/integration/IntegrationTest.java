package integration;

import com.automation.remarks.junit5.VideoExtension;
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

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Configuration.browserSize;
import static com.codeborne.selenide.Configuration.clickViaJs;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Configuration.downloadsFolder;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Configuration.versatileSetValue;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS;

@ExtendWith({ScreenShooterExtension.class, VideoExtension.class})
public abstract class IntegrationTest extends BaseIntegrationTest {
  @BeforeAll
  static void resetSettingsBeforeClass() {
    resetSettings();
  }

  @BeforeEach
  @AfterEach
  final void setUpEach() {
    resetSettings();
  }

  @BeforeEach
  @AfterEach
  final void restoreDefaultProperties() {
    timeout = 1;
    clickViaJs = false;
    downloadsFolder = "build/downloads";
  }

  @AfterAll
  public static void restartUnstableWebdriver() {
    if (isIE()) {
      closeWebDriver();
    }
  }

  private static void resetSettings() {
    timeout = 1;
    Configuration.browser = System.getProperty("selenide.browser", CHROME);
    Configuration.baseUrl = getBaseUrl();
    Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));
    Configuration.reportsFolder = System.getProperty("selenide.reportsFolder", "build/reports/tests");
    fastSetValue = false;
    versatileSetValue = false;
    browserSize = System.getProperty("selenide.browserSize", "1200x960");
    Configuration.proxyPort = 0;
    Configuration.proxyHost = "";
    useProxy(false);
    Configuration.fileDownload = HTTPGET;
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
  protected static void useProxy(boolean proxyEnabled) {
    if (Configuration.proxyEnabled != proxyEnabled) {
      Selenide.closeWebDriver();
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
    options.setCapability(ACCEPT_SSL_CERTS, true);
    options.setCapability(ACCEPT_INSECURE_CERTS, true);
  }
}
