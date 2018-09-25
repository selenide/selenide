package integration.testng;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import integration.server.LocalHttpServer;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;

import java.util.logging.Logger;

import static org.openqa.selenium.net.PortProber.findFreePort;

abstract class BaseTestNGTest {
  private static final Logger log = Logger.getLogger(BaseTestNGTest.class.getName());
  private static LocalHttpServer server;

  protected static final String browser = System.getProperty("selenide.browser", "htmlunit");
  protected static String baseUrl;

  protected SelenideDriver driver = new SelenideDriver(
    new SelenideConfig().browser(browser).baseUrl(baseUrl)
  );

  @BeforeClass
  private void startServer() throws Exception {
    if (server == null) {
      int port = findFreePort();
      log.info("START " + browser + " Test NG tests");
      server = new LocalHttpServer(port, false).start();
      baseUrl = "http://127.0.0.1:" + port;
    }
  }

  protected SelenideElement $(String cssSelector) {
    return driver.$(cssSelector);
  }

  protected SelenideElement $(By locator) {
    return driver.$(locator);
  }

  public ElementsCollection $$(String cssSelector) {
    return driver.$$(cssSelector);
  }
}
