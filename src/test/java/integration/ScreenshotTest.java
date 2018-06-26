package integration;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScreenshotTest extends IntegrationTest {

  @BeforeEach
  void openTestPage() {
    Assumptions.assumeFalse(isHtmlUnit());
    openFile("page_with_big_divs.html");
  }

  @Test
  void canTakeScreenshotOfElement() throws IOException {
    SelenideElement element = $("#small_div");
    File screenshot = element.screenshot();
    String info = "(Screenshot of element: " + screenshot.getAbsolutePath() + ") ";
    String screenshotPath = IS_OS_WINDOWS ? Configuration.reportsFolder.replace("/", "\\") : Configuration.reportsFolder;

    BufferedImage img = ImageIO.read(screenshot);
    assertEquals(img.getWidth(), element.getSize().getWidth(), "Screenshot doesn't fit width " + info);
    assertEquals(img.getHeight(), element.getSize().getHeight(), "Screenshot doesn't fit height " + info);
    assertTrue(screenshot.getPath().startsWith(screenshotPath),
      String.format("Screenshot file should be located in %s, but was: %s", screenshotPath, screenshot.getPath()));
  }

  @Test
  @Disabled
  void resizeBigImageWidth() {
    SelenideElement element = $("#wide_div");
    BufferedImage img = element.screenshotAsImage();
    assertThat("Screenshot doesn't fit width", img.getWidth(), is(lessThan(element.getSize().getWidth())));
  }

  @Test
  @Disabled
  void resizeBigImageHeight() {
    SelenideElement element = $("#big_div");
    BufferedImage img = new ScreenShotLaboratory().takeScreenshotAsImage(element);
    assertThat("Screenshot doesn't fit height", img.getHeight(), is(lessThan(element.getSize().getHeight())));
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

    assertThat("Screenshot doesn't fit width - " + errorDetails,
      img.getWidth(), is(lessThan(element.getSize().getWidth())));
    assertThat("Screenshot doesn't fit height - " + errorDetails,
      img.getHeight(), is(lessThan(element.getSize().getHeight())));
  }
}
