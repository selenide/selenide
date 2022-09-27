package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.title;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static integration.ScreenshotTestHelper.verifyScreenshotSize;
import static org.assertj.core.api.Assumptions.assumeThat;

public class FullScreenshotsGridTest extends AbstractGridTest {
  private static final int width = 2200;
  private static final int height = 3300;

  @BeforeAll
  static void beforeAll() {
    assumeThat(isEdge()).as("Edge throws 'unknown command: session/*/goog/cdp/execute'").isFalse();
    assumeThat(isFirefox()).as("Broken after upgrading to Firefox 105").isFalse();
  }

  @BeforeEach
  void setUp() {
    Configuration.remote = gridUrl.toString();
  }

  @AfterEach
  void tearDown() {
    Configuration.remote = null;
  }

  @Test
  void canTakeFullScreenshotWithTwoTabs() throws IOException {
    openFile("page_of_fixed_size_2200x3300.html");
    webdriver().shouldHave(title("Test::page of fixed size 2200x3300"));

    Selenide.executeJavaScript("window.open()");
    Selenide.switchTo().window(1);
    openFile("file_upload_form.html");

    Selenide.switchTo().window(0);

    File screenshot = Selenide.screenshot(OutputType.FILE);
    verifyScreenshotSize(screenshot, width, height);
  }
}
