package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.title;
import static integration.errormessages.Helper.verifyScreenshotSize;

public class FullScreenshotsGridTest extends AbstractGridTest {
  private static final int width = 2200;
  private static final int height = 3300;

  @BeforeEach
  void setUp() {
    Configuration.remote = "http://localhost:" + hubPort + "/wd/hub";
  }

  @AfterEach
  void tearDown() {
    Configuration.remote = null;
  }

  /*
   It fails in non-local Chromium.
   Probably it failed because here
   https://github.com/SeleniumHQ/selenium/blob/trunk/java/src/org/openqa/selenium/devtools/DevTools.java#L99
   wrong targedId detected and devtools sends Page.captureScreenshot to an inactive page that leads TimeoutException.
   */
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
