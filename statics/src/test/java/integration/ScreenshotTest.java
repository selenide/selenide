package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.assertj.core.api.Assertions.assertThat;

final class ScreenshotTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_big_divs.html");
  }

  @Test
  void canTakeScreenshotOfElement() throws IOException {
    SelenideElement element = $("#small_div");
    File screenshot = element.screenshot();
    String info = "(Screenshot of element: " + screenshot.getAbsolutePath() + ") ";
    String screenshotPath = IS_OS_WINDOWS ? Configuration.reportsFolder.replace("/", "\\") : Configuration.reportsFolder;

    BufferedImage img = ImageIO.read(screenshot);
    assertThat(element.getSize().getWidth())
      .withFailMessage("Screenshot doesn't fit width " + info)
      .isEqualTo(img.getWidth());
    assertThat(element.getSize().getHeight())
      .withFailMessage("Screenshot doesn't fit height " + info)
      .isEqualTo(img.getHeight());
    assertThat(screenshot.getPath())
      .withFailMessage(String.format("Screenshot file should be located in %s, but was: %s", screenshotPath, screenshot.getPath()))
      .startsWith(screenshotPath);
  }

  @Test
  @Disabled
  void resizeBigImageWidth() {
    SelenideElement element = $("#wide_div");
    BufferedImage img = element.screenshotAsImage();
    assertThat(img.getWidth())
      .withFailMessage("Screenshot doesn't fit width")
      .isLessThan(element.getSize().getWidth());
  }

  @Test
  @Disabled
  void resizeBigImageHeight() {
    SelenideElement element = $("#big_div");
    BufferedImage img = ScreenShotLaboratory.getInstance().takeScreenshotAsImage(null, element);
    assertThat(img.getHeight())
      .withFailMessage("Screenshot doesn't fit height")
      .isLessThan(element.getSize().getHeight());
  }

  @Test
  @Disabled
  void resizeBigImage() throws IOException {
    SelenideElement element = $("#huge_div");
    BufferedImage img = $("#huge_div").screenshotAsImage();

    byte[] screen = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
    BufferedImage tmp = ImageIO.read(new ByteArrayInputStream(screen));
    String errorDetails = String.format("element.location: %s, element.size: %s, screen.size: (%s,%s)",
      element.getLocation(), element.getSize(), tmp.getWidth(), tmp.getHeight());

    assertThat(img.getWidth())
      .withFailMessage("Screenshot doesn't fit width - " + errorDetails)
      .isLessThan(element.getSize().getWidth());
    assertThat(img.getHeight())
      .withFailMessage("Screenshot doesn't fit height - " + errorDetails)
      .isLessThan(element.getSize().getHeight());
  }
}
