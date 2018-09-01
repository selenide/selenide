package integration;

import com.automation.remarks.junit5.VideoExtension;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.codeborne.selenide.junit5.TextReportExtension;
import integration.server.LocalHttpServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

import static com.automation.remarks.video.enums.RecordingMode.ANNOTATED;
import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.browserSize;
import static com.codeborne.selenide.Configuration.clickViaJs;
import static com.codeborne.selenide.Configuration.collectionsTimeout;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Configuration.versatileSetValue;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static com.codeborne.selenide.WebDriverRunner.isSafari;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.openqa.selenium.net.PortProber.findFreePort;

@ExtendWith({ScreenShooterExtension.class, TextReportExtension.class, VideoExtension.class})
public abstract class IntegrationTest implements WithAssertions {
  private static final Logger log = Logger.getLogger(IntegrationTest.class.getName());
  // http or https
  private static final boolean SSL = false;
  protected static LocalHttpServer server;
  static long averageSeleniumCommandDuration = 100;
  private static String protocol;
  private static int port;
  private long defaultTimeout;
  private long defaultCollectionsTimeout;

  @BeforeAll
  static void setUpAll() throws Exception {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tT %4$s %5$s%6$s%n"); // add %2$s for source
    Locale.setDefault(Locale.ENGLISH);
    runLocalHttpServer();
    setUpVideoRecorder();
  }

  private static void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (IntegrationTest.class) {
        port = findFreePort();
        log.info("START " + browser + " TESTS");
        server = new LocalHttpServer(port, SSL).start();
        protocol = SSL ? "https://" : "http://";
      }
    }
  }

  private static void setUpVideoRecorder() {
    File videoFolder = new File("build/reports/tests/" + Configuration.browser);
    videoFolder.mkdirs();
    System.setProperty("video.folder", videoFolder.getAbsolutePath());
    System.setProperty("video.enabled", String.valueOf(!isHeadless()));
    System.setProperty("video.mode", String.valueOf(ANNOTATED));
  }

  @AfterAll
  public static void restartUnstableWebdriver() {
    if (isIE() || isPhantomjs()) {
      closeWebDriver();
    }
  }

  @BeforeEach
  void setUpEach() {
    resetSettings();
    restartReallyUnstableBrowsers();
    rememberTimeout();
  }

  private void resetSettings() {
    Configuration.browser = System.getProperty("selenide.browser", FIREFOX);
    Configuration.baseUrl = protocol + "127.0.0.1:" + port;
    Configuration.reportsFolder = "build/reports/tests/" + Configuration.browser;
    Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));
    fastSetValue = false;
    versatileSetValue = false;
    browserSize = "1200x960";
    server.reset();
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
    open("/" + fileName + "?browser=" + Configuration.browser +
      "&timeout=" + Configuration.timeout);
  }

  <T> T openFile(String fileName, Class<T> pageObjectClass) {
    return open("/" + fileName + "?browser=" + Configuration.browser +
      "&timeout=" + Configuration.timeout, pageObjectClass);
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
