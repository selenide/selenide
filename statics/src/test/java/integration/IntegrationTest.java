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

import java.util.logging.Logger;

import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.Configuration.browserSize;
import static com.codeborne.selenide.Configuration.clickViaJs;
import static com.codeborne.selenide.Configuration.collectionsTimeout;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Configuration.setValueChangeEvent;
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
  private static final Logger log = Logger.getLogger(IntegrationTest.class.getName());

  private long defaultTimeout;
  private long defaultCollectionsTimeout;

  @BeforeAll
  static void logBrowserName() {
    log.info("START " + browser + " TESTS");
  }

  @AfterAll
  public static void restartUnstableWebdriver() {
    if (isIE() || isPhantomjs()) {
      closeWebDriver();
    }
  }

  @BeforeEach
  final void setUpEach() {
    resetSettings();
    restartReallyUnstableBrowsers();
    rememberTimeout();
  }

  private void resetSettings() {
    Configuration.browser = System.getProperty("selenide.browser", FIREFOX);
    Configuration.baseUrl = getBaseUrl();
    Configuration.reportsFolder = "build/reports/tests/" + browser;
    Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));
    fastSetValue = false;
    versatileSetValue = false;
    setValueChangeEvent = true;
    browserSize = "1200x960";
    Configuration.proxyPort = 0;
    Configuration.proxyHost = "";
    toggleProxy(!isPhantomjs());
  }

  private void restartReallyUnstableBrowsers() {
    if (isSafari()) {
      closeWebDriver();
    }
  }

  private void rememberTimeout() {
    defaultTimeout = timeout;
    defaultCollectionsTimeout = collectionsTimeout;
  }

  protected void openFile(String fileName) {
    open("/" + fileName + "?browser=" + browser +
      "&timeout=" + timeout);
  }

  <T> T openFile(String fileName, Class<T> pageObjectClass) {
    return open("/" + fileName + "?browser=" + browser +
      "&timeout=" + timeout, pageObjectClass);
  }

  protected void toggleProxy(boolean proxyEnabled) {
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

  @AfterEach
  public void restoreDefaultProperties() {
    timeout = defaultTimeout;
    collectionsTimeout = defaultCollectionsTimeout;
    clickViaJs = false;
  }
}
