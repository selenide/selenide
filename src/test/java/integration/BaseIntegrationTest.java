package integration;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.junit5.TextReportExtension;
import com.codeborne.selenide.webdriver.HttpClientTimeouts;
import integration.server.LocalHttpServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.SessionNotCreatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.SAFARI;
import static integration.server.LocalHttpServer.startWithRetry;
import static java.lang.Boolean.parseBoolean;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

@ExtendWith({LogTestNameExtension.class, TextReportExtension.class})
public abstract class BaseIntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(BaseIntegrationTest.class);

  protected static volatile LocalHttpServer server;
  private static String protocol;
  protected static final String browser = System.getProperty("selenide.browser", CHROME);
  private static final boolean SSL = !SAFARI.equalsIgnoreCase(browser);
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

  @BeforeEach
  final void resetTimeouts() {
    HttpClientTimeouts.defaultLocalReadTimeout = Duration.ofSeconds(30);
    HttpClientTimeouts.defaultLocalConnectTimeout = Duration.ofSeconds(10);
  }

  private static void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (BaseIntegrationTest.class) {
        if (server == null) {
          protocol = SSL ? "https://" : "http://";
          server = startWithRetry(SSL, "no-cors-allowed", Map.of("scott", scottPassword(), "john", johnPassword()));
          log.info("Server started at {}", getBaseUrl());
        }
      }
    }
  }

  protected static String domain() {
    return "127.0.0.1";
  }

  protected static String scottPassword() {
    return "tiger://<script>alert(1)</script>&\\";
  }

  protected static String johnPassword() {
    return "mcclane://<script>console.log(2);&amp;-</script>&";
  }

  protected static String getBaseUrl() {
    return protocol + domain() + ":" + server.getPort();
  }

  protected static String getProtectedUrl(String username, String password, String path) {
    return protocol + encode(username) + ':' + encode(password) + '@' + domain() + ":" + server.getPort() + path;
  }

  protected static String encode(String value) {
    return URLEncoder.encode(value, UTF_8);
  }

  protected static Browser browser() {
    return new Browser(browser, headless);
  }

  protected void retry(Runnable block, int maxRetries) {
    SessionNotCreatedException lastError = null;
    for (int i = 1; i <= maxRetries; i++) {
      try {
        block.run();
        return;
      }
      catch (SessionNotCreatedException failedToOpenBrowser) {
        lastError = failedToOpenBrowser;
        log.error("Failed to open browser #{}: {}", i, failedToOpenBrowser.getMessage(), failedToOpenBrowser);
      }
    }
    throw requireNonNull(lastError);
  }
}
