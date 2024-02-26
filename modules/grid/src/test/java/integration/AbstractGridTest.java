package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.grid.Main;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.openqa.selenium.net.PortProber.findFreePort;

abstract class AbstractGridTest extends IntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(AbstractGridTest.class);

  protected static URL gridUrl;

  @BeforeEach
  @AfterEach
  final void resetWebDriver() {
    closeWebDriver();
    timeout = 4000;
    Configuration.remote = null;
    WebDriverRunner.addListener(new WebDriverListener() {
      @Override
      public void beforeAnyCall(Object target, Method method, Object[] args) {
        if ("quit".equals(method.getName())) {
          log.debug("before call {}", method);
        }
      }
    });
  }

  @BeforeAll
  static synchronized void setUpGrid() throws MalformedURLException {
    if (gridUrl == null) {
      for (int tries = 0; tries < 3; tries++) {
        int port = findFreePort();
        try {
          String username = "secret";
          String password = "Love.Is.Stronger.Than.Fear";
          Main.main(new String[]{
            "standalone",
            "--port", String.valueOf(port),
            "--enable-managed-downloads", "true",
            "--selenium-manager", "true",
            "--username", username,
            "--password", password
          });
          gridUrl = new URL("http://%s:%s@localhost:%s/wd/hub".formatted(encode(username), encode(password), port));
          break;
        }
        catch (UncheckedIOException portAlreadyUsed) {
          log.warn("Failed to start Selenium Grid on port {}", port, portAlreadyUsed);
        }
      }
    }
  }
}
