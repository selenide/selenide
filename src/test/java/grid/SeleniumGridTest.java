package grid;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.close;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.openqa.selenium.net.PortProber.findFreePort;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import integration.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.grid.selenium.GridLauncherV3;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumGridTest extends IntegrationTest {
  @Before
  public void setUp() {
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
  public void canUseSeleniumGrid() {
    openFile("page_with_selects_without_jquery.html");
    $$("#radioButtons input").shouldHave(size(4));
  }

  @Test
  public void shouldUseLocalFileDetector() {
    RemoteWebDriver webDriver = (RemoteWebDriver) WebDriverRunner.getWebDriver();
    assertThat(webDriver.getFileDetector(), instanceOf(LocalFileDetector.class));
  }

  @After
  public void tearDown() {
    close();
    Configuration.remote = null;
  }
}
