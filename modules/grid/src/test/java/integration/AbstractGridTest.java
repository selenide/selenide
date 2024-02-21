package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.grid.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
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
  }

  @BeforeAll
  static synchronized void setUpGrid() throws MalformedURLException {
    if (gridUrl == null) {
      for (int tries = 0; tries < 3; tries++) {
        int port = findFreePort();
        try {
          Main.main(new String[]{
            "standalone",
            "--port", String.valueOf(port),
            "--enable-managed-downloads", "true",
            "--selenium-manager", "true"
          });
          gridUrl = new URL("http://localhost:" + port + "/wd/hub");
          break;
        }
        catch (UncheckedIOException portAlreadyUsed) {
          log.warn("Failed to start Selenium Grid on port {}", port, portAlreadyUsed);
        }
      }
    }
  }
}
