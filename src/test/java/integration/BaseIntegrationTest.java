package integration;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.junit5.TextReportExtension;
import integration.server.LocalHttpServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

import static com.automation.remarks.video.enums.RecordingMode.ANNOTATED;
import static com.codeborne.selenide.Browsers.FIREFOX;
import static java.lang.Boolean.parseBoolean;
import static org.openqa.selenium.net.PortProber.findFreePort;

@ExtendWith({TextReportExtension.class})
public abstract class BaseIntegrationTest implements WithAssertions {
  private static final Logger log = Logger.getLogger(BaseIntegrationTest.class.getName());

  private static final boolean SSL = false;
  protected static LocalHttpServer server;
  protected static long averageSeleniumCommandDuration = 100;
  private static String protocol;
  private static int port;
  protected static final String browser = System.getProperty("selenide.browser", FIREFOX);
  private static final boolean headless = parseBoolean(System.getProperty("selenide.headless", "false"));

  @BeforeAll
  static void setUpAll() throws Exception {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tT %4$s %5$s%6$s%n"); // add %2$s for source
    Locale.setDefault(Locale.ENGLISH);
    runLocalHttpServer();
    setUpVideoRecorder();
  }

  @BeforeAll
  static void logBrowserName() {
    log.info("START " + browser + (headless ? " (headless)" : "") + " TESTS");
  }

  @BeforeEach
  final void resetUploadedFiles() {
    server.reset();
  }

  private static void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (BaseIntegrationTest.class) {
        port = findFreePort();
        server = new LocalHttpServer(port, SSL).start();
        protocol = SSL ? "https://" : "http://";
      }
    }
  }

  private static void setUpVideoRecorder() {
    File videoFolder = new File(System.getProperty("selenide.reportsFolder", "build/reports/tests"));
    videoFolder.mkdirs();
    System.setProperty("video.folder", videoFolder.getAbsolutePath());
    System.setProperty("video.enabled", String.valueOf(!browser().isHeadless()));
    System.setProperty("video.mode", String.valueOf(ANNOTATED));
  }

  protected static String getBaseUrl() {
    return protocol + "127.0.0.1:" + port;
  }

  protected static Browser browser() {
    return new Browser(browser, headless);
  }
}
