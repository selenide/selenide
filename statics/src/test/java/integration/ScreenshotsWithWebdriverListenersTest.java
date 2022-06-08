package integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.WebDriverListener;

// Tests will fail because listeners decorate webdriver instance.
public class ScreenshotsWithWebdriverListenersTest extends IntegrationTest {
  private final DeprecatedListener deprecatedListener = new DeprecatedListener();
  private final Selenium4Listener listener = new Selenium4Listener();

  @AfterEach
  @BeforeEach
  void tearDown() {
    WebDriverRunner.removeListener(listener);
    WebDriverRunner.removeListener(deprecatedListener);
    Selenide.closeWebDriver();
  }

  @Test
  void canTakeFullScreenshotWithSelenium4Listeners() throws IOException {
    WebDriverRunner.addListener(listener);
    openFile("page_with_selects_without_jquery.html");

    File screenshot = Selenide.screenshot(OutputType.FILE);
    BufferedImage img = ImageIO.read(screenshot);

    assertThat(img.getWidth()).isBetween(2200, 3000);
    assertThat(img.getHeight()).isBetween(2400, 3000);
  }

  @Test
  void canTakeFullScreenshotWithSelenium3Listeners() throws IOException {
    WebDriverRunner.addListener(deprecatedListener);
    openFile("page_with_selects_without_jquery.html");

    File screenshot = Selenide.screenshot(OutputType.FILE);
    BufferedImage img = ImageIO.read(screenshot);

    assertThat(img.getWidth()).isBetween(2200, 3000);
    assertThat(img.getHeight()).isBetween(2400, 3000);
  }

  public static class Selenium4Listener implements WebDriverListener {

  }

  public static class DeprecatedListener extends AbstractWebDriverEventListener {

  }
}
