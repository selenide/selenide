package grid;

import integration.IntegrationTest;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.grid.selenium.GridLauncherV3;
import org.openqa.grid.shared.Stoppable;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.openqa.selenium.net.PortProber.findFreePort;

abstract class AbstractGridTest extends IntegrationTest {
  private Stoppable gridHub;
  private Stoppable gridNode;
  int hubPort;

  @BeforeEach
  final void setUpGrid() {
    closeWebDriver();

    hubPort = findFreePort();
    gridHub = new GridLauncherV3().launch(new String[]{"-port", "" + hubPort});

    gridNode = new GridLauncherV3().launch(new String[]{"-port", "" + findFreePort(),
      "-role", "node",
      "-hub", "http://localhost:" + hubPort + "/grid/register"
    });

    WebDriverManager.chromedriver().setup();
    timeout = 4000;
  }

  @AfterEach
  final void tearDownGrid() {
    closeWebDriver();
    gridHub.stop();
    gridNode.stop();
  }
}
