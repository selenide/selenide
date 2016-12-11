package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class ScreenshotTest extends IntegrationTest {

  @Before
  public void openTestPage() {
    assumeFalse(isHtmlUnit());
    openFile("page_with_big_divs.html");
  }

  @Test
  public void canTakeScreenshotOfElement() throws IOException {
    SelenideElement element = $("#small_div");
    File screenshot = element.screenshot();
    String info = "(Screenshot of element: " + screenshot.getAbsolutePath() + ") ";

    BufferedImage img = ImageIO.read(screenshot);
    assertEquals("Screenshot doesn't fit width " + info, img.getWidth(), element.getSize().getWidth());
    assertEquals("Screenshot doesn't fit height " + info, img.getHeight(),  element.getSize().getHeight());
    assertTrue("Screenshot file should be located in " + Configuration.reportsFolder +
        ", but was: " + screenshot.getPath(),
        screenshot.getPath().startsWith(Configuration.reportsFolder));
  }

  @Test
  public void testResizeBigImageWidth() {
    SelenideElement element = $("#wide_div");
    BufferedImage img = element.screenshotAsImage();
    assertTrue("Screenshot doesn't fit width", img.getWidth() < element.getSize().getWidth());
  }

  @Test
  public void testResizeBigImageHeight() {
    SelenideElement element = $("#big_div");
    BufferedImage img =  new ScreenShotLaboratory().takeScreenshotAsImage(element);
    assertTrue("Screenshot doesn't fit height", img.getHeight() < element.getSize().getHeight());
  }

  @Test
  public void testResizeBigImage() {
    SelenideElement element = $("#huge_div");
    BufferedImage img = $("#huge_div").screenshotAsImage();
    assertTrue("Screenshot doesn't fit width", img.getWidth() < element.getSize().getWidth());
    assertTrue("Screenshot doesn't fit height", img.getHeight() < element.getSize().getHeight());
  }
}
