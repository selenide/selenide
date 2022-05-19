package integration;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.junit5.TextReportExtension;
import integration.server.LocalHttpServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.BindException;
import java.util.Locale;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.Browsers.SAFARI;
import static java.lang.Boolean.parseBoolean;
import static org.openqa.selenium.net.PortProber.findFreePort;

@ExtendWith({LogTestNameExtension.class, TextReportExtension.class})
public abstract class BaseIntegrationTest {
  protected static volatile LocalHttpServer server;
  private static String protocol;
  protected static final String browser = System.getProperty("selenide.browser", CHROME);
  private static final boolean SSL = !SAFARI.equalsIgnoreCase(browser) && !FIREFOX.equalsIgnoreCase(browser);
  static final boolean headless = parseBoolean(System.getProperty("selenide.headless", "false"));
  private static final Locale defaultLocale = Locale.getDefault();

  @BeforeAll
  static void setUpAll() throws Exception {
    Locale.setDefault(defaultLocale);
    runLocalHttpServer();
  }

  @BeforeEach
  final void resetUploadedFiles() {
    server.reset();
  }

  private static void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (BaseIntegrationTest.class) {
        if (server == null) {
          protocol = SSL ? "https://" : "http://";
          server = runWithRetry();
        }
      }
    }
  }

  private static LocalHttpServer runWithRetry() throws Exception {
    IOException lastError = null;
    for (int i = 0; i < 5; i++) {
      try {
        return new LocalHttpServer(findFreePort(), SSL).start();
      }
      catch (IOException failedToStartServer) {
        if (failedToStartServer.getCause() instanceof BindException) {
          lastError = failedToStartServer;
        }
        else {
          throw failedToStartServer;
        }
      }
    }
    throw lastError;
  }

  protected static String getBaseUrl() {
    return protocol + "127.0.0.1:" + server.getPort();
  }

  protected static Browser browser() {
    return new Browser(browser, headless);
  }
}
