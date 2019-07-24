package grid;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.webdriver.WebDriverBinaryManager;
import integration.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.grid.selenium.GridLauncherV3;
import org.openqa.grid.shared.Stoppable;

import static com.codeborne.selenide.Selenide.close;
import static org.openqa.selenium.net.PortProber.findFreePort;

abstract class AbstractGridTest extends IntegrationTest {
  private Stoppable gridHub;
  private Stoppable gridNode;
  protected int hubPort;

  @BeforeEach
  final void setUpGrid() {
    close();

    hubPort = findFreePort();
    gridHub = new GridLauncherV3().launch(new String[]{"-port", "" + hubPort});

    gridNode = new GridLauncherV3().launch(new String[]{"-port", "" + findFreePort(),
      "-role", "node",
      "-hub", "http://localhost:" + hubPort + "/grid/register"
    });

    new WebDriverBinaryManager().setupBinaryPath(new Browser(Configuration.browser, Configuration.headless));
  }

  @AfterEach
  final void tearDownGrid() {
    close();
    gridHub.stop();
    gridNode.stop();
  }
}
