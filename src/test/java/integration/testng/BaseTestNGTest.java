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
      server = new LocalHttpServer(port, false).start();
      baseUrl = "http://127.0.0.1:" + port;
    }

    open("/start_page.html");
  }
}
