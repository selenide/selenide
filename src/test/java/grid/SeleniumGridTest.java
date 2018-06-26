package grid;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import integration.IntegrationTest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.grid.selenium.GridLauncherV3;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.close;
import static org.hamcrest.Matchers.instanceOf;
import static org.openqa.selenium.net.PortProber.findFreePort;

class SeleniumGridTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    close();

    int hubPort = findFreePort();
    GridLauncherV3.main(new String[]{"-port", "" + hubPort});

    GridLauncherV3.main(new String[]{"-port", "" + findFreePort(),
      "-role", "node",
      "-hub", "http://localhost:" + hubPort + "/grid/register"
    });

    Configuration.remote = "http://localhost:" + hubPort + "/wd/hub";
    Configuration.browser = "htmlunit";
  }

  @Test
  void canUseSeleniumGrid() {
    openFile("page_with_selects_without_jquery.html");
    $$("#radioButtons input").shouldHave(size(4));
  }

  @Test
  void shouldUseLocalFileDetector() {
    RemoteWebDriver webDriver = (RemoteWebDriver) WebDriverRunner.getWebDriver();
    MatcherAssert.assertThat(webDriver.getFileDetector(), instanceOf(LocalFileDetector.class));
  }

  @AfterEach
  void tearDown() {
    close();
    Configuration.remote = null;
  }
}
