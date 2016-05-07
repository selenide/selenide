package integration.testng;

import com.codeborne.selenide.testng.annotations.*;
import integration.*;
import org.testng.annotations.*;

import java.util.logging.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.*;
import static org.openqa.selenium.net.PortProber.*;

@Report
public class aSimpleNGTest {
  private static final Logger log = Logger.getLogger(aSimpleNGTest.class.getName());
  private static int port;
  protected static LocalHttpServer server;

  @BeforeClass
  public void setUp() throws Exception {
    if (server == null) {
      port = findFreePort();
      server = new LocalHttpServer(port).start();
      log.info("START " + browser + " TESTS");
      baseUrl = "https://127.0.0.1:" + port;
    }


    open("/start_page.html");
  }


  @Test
  public void successfulMethod() {
    $("h1").shouldBe(visible, text("Selenide"));
  }

}
