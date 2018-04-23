package integration;

import com.automation.remarks.junit.VideoRule;
import com.automation.remarks.video.recorder.VideoRecorder;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit.ScreenShooter;
import com.codeborne.selenide.junit.TextReport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;

import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

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

public abstract class IntegrationTest {
  static {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tT %4$s %5$s%6$s%n"); // add %2$s for source
    Locale.setDefault(Locale.ENGLISH);
  }

  private static final Logger log = Logger.getLogger(IntegrationTest.class.getName());
  // http or https
  private static final boolean SSL = false;
  private static String protocol;

  @Rule
  public ScreenShooter img = ScreenShooter.failedTests();

  @Rule
  public TestRule report = new TextReport().onFailedTest(true).onSucceededTest(true);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Rule public VideoRule video = new VideoRule();

  private static int port;
  protected static LocalHttpServer server;
  private long defaultTimeout;
  protected static long averageSeleniumCommandDuration = 100;

  @BeforeClass
  public static void runLocalHttpServer() throws Exception {
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

  @Before
  public void restartReallyUnstableBrowsers() {
    if (isSafari()) {
      closeWebDriver();
    }
  }

  @Before
  public void resetSettings() {
    Configuration.baseUrl = protocol + "127.0.0.1:" + port;
    Configuration.reportsFolder = "build/reports/tests/" + Configuration.browser;
    fastSetValue = false;
    browserSize = "1024x768";
    server.uploadedFiles.clear();

    // proxy breaks Firefox/Marionette because of this error:
    // "InvalidArgumentError: Expected [object Undefined] undefined to be an integer"
    Configuration.fileDownload = isFirefox() || isLegacyFirefox() ? HTTPGET : PROXY;
  }

  @BeforeClass
  public static void setUpVideoRecorder() {
    File videoFolder = new File("build/reports/tests/" + Configuration.browser);
    videoFolder.mkdirs();
    System.setProperty("video.folder", videoFolder.getAbsolutePath());
    VideoRecorder.conf()
        .withVideoFolder(videoFolder.getAbsolutePath())
        .videoEnabled(!isHeadless())
        .withRecordMode(ANNOTATED);
  }

  @AfterClass
  public static void restartUnstableWebdriver() {
    if (isIE() || isPhantomjs()) {
      closeWebDriver();
    }
  }

  protected void openFile(String fileName) {
    open("/" + fileName + "?browser=" + Configuration.browser +
        "&timeout=" + Configuration.timeout);
  }

  protected <T> T openFile(String fileName, Class<T> pageObjectClass) {
    return open("/" + fileName + "?browser=" + Configuration.browser +
        "&timeout=" + Configuration.timeout, pageObjectClass);
  }

  @Before
  public final void rememberTimeout() {
    defaultTimeout = timeout;
  }

  @After
  public final void restoreDefaultProperties() {
    timeout = defaultTimeout;
    clickViaJs = false;
  }
}
