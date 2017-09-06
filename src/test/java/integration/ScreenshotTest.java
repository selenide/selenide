package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
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
    assertEquals("Screenshot doesn't fit height " + info, img.getHeight(), element.getSize().getHeight());
    assertTrue("Screenshot file should be located in " + Configuration.reportsFolder +
            ", but was: " + screenshot.getPath(),
        screenshot.getPath().startsWith(Configuration.reportsFolder));
  }

  @Test
  @Ignore
  public void resizeBigImageWidth() {
    SelenideElement element = $("#wide_div");
    BufferedImage img = element.screenshotAsImage();
    assertThat("Screenshot doesn't fit width", img.getWidth(), is(lessThan(element.getSize().getWidth())));
  }

  @Test
  @Ignore
  public void resizeBigImageHeight() {
    SelenideElement element = $("#big_div");
    BufferedImage img = new ScreenShotLaboratory().takeScreenshotAsImage(element);
    assertThat("Screenshot doesn't fit height", img.getHeight(), is(lessThan(element.getSize().getHeight())));
  }

  @Test
  @Ignore
  public void resizeBigImage() throws IOException {
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
