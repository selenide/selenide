package integration.testng;

import com.codeborne.selenide.testng.*;
import integration.*;
import org.testng.annotations.*;

import java.util.logging.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.*;
import static org.openqa.selenium.net.PortProber.*;

@Listeners(TextReport.class)
public class ReportsNGTest {
  private static final Logger log = Logger.getLogger(ReportsNGTest.class.getName());
  private static int port;
  protected static LocalHttpServer server;

  @BeforeClass
  public void setUp() throws Exception {
    if (server == null) {
      port = findFreePort();
      server = new LocalHttpServer(port).start();
      log.info("START " + browser + " TESTS");
      com.codeborne.selenide.Configuration.baseUrl = "https://127.0.0.1:" + port;
    }


    open("/start_page.html");
  }

  @Test(expectedExceptions = Error.class)
  public void failingMethod() {
    $("h2").shouldBe(visible, text("Selenide"));
  }

  @Test
  public void successfulMethod() {
    $("h1").shouldBe(visible, text("Selenide"));
  }

  @Test
  public void reportingCollections() {
    $$("h1").shouldHaveSize(1);
    $$("h2").shouldHaveSize(1);
    $("h1").shouldBe(visible);
  }
}
