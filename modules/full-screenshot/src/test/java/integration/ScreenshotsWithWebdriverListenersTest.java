package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.events.WebDriverListener;

import java.io.File;
import java.io.IOException;

import static integration.ScreenshotTestHelper.verifyScreenshotSize;

public class ScreenshotsWithWebdriverListenersTest extends IntegrationTest {
  private final EmptyListener listener = new EmptyListener();
  private final int width = 2200;
  private final int height = 3300;

  @AfterEach
  @BeforeEach
  void tearDown() {
    WebDriverRunner.removeListener(listener);
    Selenide.closeWebDriver();
    Configuration.browserSize = "400x300";
  }

  @Test
  void canTakeFullScreenshotWithWebdriverListeners() throws IOException {
    WebDriverRunner.addListener(listener);
    openFile("page_of_fixed_size_2200x3300.html");

    File screenshot = Selenide.screenshot(OutputType.FILE);

    verifyScreenshotSize(screenshot, width, height);
  }

  public static class EmptyListener implements WebDriverListener {
  }
}
