package integration;

import com.codeborne.selenide.Browser;
import integration.server.LocalHttpServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.Locale;

import static com.automation.remarks.video.enums.RecordingMode.ANNOTATED;
import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.SAFARI;
import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static java.lang.Boolean.parseBoolean;
import static org.openqa.selenium.net.PortProber.findFreePort;

@ExtendWith({LogTestNameExtension.class})
abstract class BaseIntegrationTest {
  protected static LocalHttpServer server;
  private static String protocol;
  private static int port;
  protected static final String browser = System.getProperty("selenide.browser", CHROME);
  private static final boolean SSL = !SAFARI.equalsIgnoreCase(browser);
  static final boolean headless = parseBoolean(System.getProperty("selenide.headless", "false"));

  @BeforeAll
  static void setUpAll() throws Exception {
    Locale.setDefault(Locale.ENGLISH);
    runLocalHttpServer();
    setUpVideoRecorder();
  }

  @BeforeEach
  final void resetUploadedFiles() {
    server.reset();
  }

  private static void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (BaseIntegrationTest.class) {
        if (server == null) {
          port = findFreePort();
          server = new LocalHttpServer(port, SSL).start();
          protocol = SSL ? "https://" : "http://";
        }
      }
    }
  }

  private static void setUpVideoRecorder() {
    File videoFolder = new File(System.getProperty("selenide.reportsFolder", "build/reports/tests"));
    ensureFolderExists(videoFolder);
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
