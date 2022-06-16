package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.WebDriverListener;

import java.io.File;
import java.io.IOException;

import static integration.ScreenshotTestHelper.verifyScreenshotSize;

@SuppressWarnings("deprecation")
public class ScreenshotsWithWebdriverListenersTest extends IntegrationTest {
  private final DeprecatedListener deprecatedListener = new DeprecatedListener();
  private final Selenium4Listener listener = new Selenium4Listener();
  private final int width = 2200;
  private final int height = 3300;

  @AfterEach
  @BeforeEach
  void tearDown() {
    WebDriverRunner.removeListener(listener);
    WebDriverRunner.removeListener(deprecatedListener);
    Selenide.closeWebDriver();
    Configuration.browserSize = "400x300";
  }

  @Test
  void canTakeFullScreenshotWithSelenium4Listeners() throws IOException {
    WebDriverRunner.addListener(listener);
    openFile("page_of_fixed_size_2200x3300.html");

    File screenshot = Selenide.screenshot(OutputType.FILE);

    verifyScreenshotSize(screenshot, width, height);
  }

  @Test
  void canTakeFullScreenshotWithSelenium3Listeners() throws IOException {
    WebDriverRunner.addListener(deprecatedListener);
    openFile("page_of_fixed_size_2200x3300.html");

    File screenshot = Selenide.screenshot(OutputType.FILE);

    verifyScreenshotSize(screenshot, width, height);
  }

  public static class Selenium4Listener implements WebDriverListener {
  }

  public static class DeprecatedListener extends AbstractWebDriverEventListener {
  }
}
