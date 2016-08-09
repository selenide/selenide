package integration.testng;

import integration.LocalHttpServer;
import org.testng.annotations.BeforeClass;

import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Selenide.open;
import static org.openqa.selenium.net.PortProber.findFreePort;

abstract class BaseTestNGTest {
  private static final Logger log = Logger.getLogger(BaseTestNGTest.class.getName());
  private static LocalHttpServer server;

  @BeforeClass
  public void setUp() throws Exception {
    if (server == null) {
      int port = findFreePort();
      server = new LocalHttpServer(port).start();
      log.info("START " + browser + " TESTS");
      baseUrl = "https://127.0.0.1:" + port;
    }

    open("/start_page.html");
  }
}
