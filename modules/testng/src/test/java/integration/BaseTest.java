package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.testng.BrowserPerTest;
import com.codeborne.selenide.testng.ScreenShooter;
import com.codeborne.selenide.testng.SoftAsserts;
import com.codeborne.selenide.testng.TextReport;
import integration.server.LocalHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import static com.codeborne.selenide.AssertionMode.STRICT;
import static org.openqa.selenium.net.PortProber.findFreePort;

@Listeners({SoftAsserts.class, TextReport.class, BrowserPerTest.class, ScreenShooter.class})
abstract class BaseTest {
  private static final Logger log = LoggerFactory.getLogger(BaseTest.class);
  private static LocalHttpServer server;

  @BeforeSuite
  void setupAsserts() {
    Configuration.assertionMode = STRICT;
  }

  @BeforeSuite
  final void startServer() throws Exception {
    if (server == null) {
      int port = findFreePort();
      log.info("START {} Test NG tests", Configuration.browser);
      server = new LocalHttpServer(port, true).start();
      Configuration.baseUrl = "https://127.0.0.1:" + port;
    }
  }
}
