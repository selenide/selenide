package integration.grid;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import integration.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.grid.selenium.GridLauncherV3;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.close;
import static org.junit.Assume.assumeFalse;
import static org.openqa.selenium.net.PortProber.findFreePort;

public class SeleniumGridTest extends IntegrationTest {
  @Before
  public void setUp() throws Exception {
    assumeFalse(WebDriverRunner.isPhantomjs()); // for unknown reason, node registration doesn't work with PhantomJS yet.
    
    close();
    
    int hubPort = findFreePort();
    GridLauncherV3.main(new String[]{"-port", "" + hubPort});

    GridLauncherV3.main(new String[]{"-port", "" + findFreePort(),
        "-role", "node",
        "-hub", "http://localhost:" + hubPort + "/grid/register"
    });

    Configuration.remote = "http://localhost:" + hubPort + "/wd/hub";
  }

  @Test
  public void canUseSeleniumGrid() {
    openFile("page_with_selects_without_jquery.html");
    $$("#radioButtons input").shouldHave(size(4));
  }

  @After
  public void tearDown() {
    close();
    Configuration.remote = null;
  }
}
