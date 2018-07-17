package integration;

import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

import com.automation.remarks.junit5.VideoExtension;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.codeborne.selenide.junit5.TextReportExtension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.automation.remarks.video.enums.RecordingMode.ANNOTATED;
import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.browserSize;
import static com.codeborne.selenide.Configuration.clickViaJs;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.isLegacyFirefox;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static com.codeborne.selenide.WebDriverRunner.isSafari;
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
        if (SSL) {
          protocol = "https://";
        } else {
          protocol = "http://";
        }
        Configuration.baseUrl = protocol + "127.0.0.1:" + port;
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
    Configuration.baseUrl = protocol + "127.0.0.1:" + port;
    Configuration.reportsFolder = "build/reports/tests/" + Configuration.browser;
    fastSetValue = false;
    browserSize = "1024x768";
    server.uploadedFiles.clear();

    // proxy breaks Firefox/Marionette because of this error:
    // "InvalidArgumentError: Expected [object Undefined] undefined to be an integer"
    Configuration.fileDownload = isFirefox() || isLegacyFirefox() ? HTTPGET : PROXY;
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
    open("/" + fileName + "?browser=" + Configuration.browser +
      "&timeout=" + Configuration.timeout);
  }

  <T> T openFile(String fileName, Class<T> pageObjectClass) {
    return open("/" + fileName + "?browser=" + Configuration.browser +
      "&timeout=" + Configuration.timeout, pageObjectClass);
  }

  @AfterEach
  public void restoreDefaultProperties() {
    timeout = defaultTimeout;
    clickViaJs = false;
  }
}
