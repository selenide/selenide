package integration.testng;

import integration.LocalHttpServer;

import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Selenide.open;
import static org.openqa.selenium.net.PortProber.findFreePort;

abstract class BaseTestNGTest {
  private static final Logger log = Logger.getLogger(BaseTestNGTest.class.getName());
  private static LocalHttpServer server;
  
  protected void startServer() throws Exception {
    if (server == null) {
      int port = findFreePort();
      log.info("START " + browser + " Test NG tests");
      baseUrl = "https://127.0.0.1:" + port;
      server = new LocalHttpServer(port).start();
    }

    open("/start_page.html");
  }
}
