package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.switchTo;
import static integration.ScreenshotTestHelper.verifyScreenshotSize;

final class ScreenshotWhileSwitchedToFrameTest extends IntegrationTest {
  private final int width = 2200;
  private final int height = 3300;

  @Test
  void takesFullPageScreenshotEvenWhenSwitchedIntoAFrame() throws IOException {
    openFile("page_of_fixed_size_with_iframe.html");
    switchTo().frame("tiny_iframe");

    File screenshot = Selenide.screenshot(OutputType.FILE);

    verifyScreenshotSize(screenshot, width, height);
  }
}
