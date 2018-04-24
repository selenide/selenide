package integration;

import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class ScreenshotInIframeTest extends IntegrationTest {

  @Before
  public void setUp() {
    assumeFalse(isHtmlUnit());
    openFile("page_with_iframe.html");
  }

  @Test
  public void canTakeScreenshotAsImageOfFullyVisibleElementInIframe() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#small_div");
    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);
    switchTo().frame(iframe);
    assertEquals(element.getSize().getWidth(), image.getWidth());
    assertEquals(element.getSize().getHeight(), image.getHeight());
    switchTo().defaultContent();
  }

  @Test
  public void canTakeScreenshotOfFullyVisibleElementInIframe() throws Exception {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#small_div");
    File file = Screenshots.takeScreenShot(iframe, element);
    BufferedImage image = ImageIO.read(file);
    switchTo().frame(iframe);
    assertEquals(element.getSize().getWidth(), image.getWidth());
    assertEquals(element.getSize().getHeight(), image.getHeight());
    switchTo().defaultContent();
  }

  @Test
  public void canTakeScreenshotAsImageOfPartiallyVisibleElementInIframe() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#wide_div");
    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);
    switchTo().frame(iframe);
    assertNotEquals(element.getSize().getWidth(), image.getWidth());
    assertEquals(element.getSize().getHeight(), image.getHeight());
    switchTo().defaultContent();
  }

  @Test
  public void canTakeScreenshotOfPartiallyVisibleElementInIframe() throws Exception {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#wide_div");
    File file = Screenshots.takeScreenShot(iframe, element);
    BufferedImage image = ImageIO.read(file);
    switchTo().frame(iframe);
    assertNotEquals(element.getSize().getWidth(), image.getWidth());
    assertEquals(element.getSize().getHeight(), image.getHeight());
    switchTo().defaultContent();
  }

  @Test
  public void screenshotAsImageOfNotVisibleElementIsNotTaken() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#big_div");
    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);
    assertTrue(image == null);
  }

  @Test
  public void screenshotOfNotVisibleElementIsNotTaken() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#big_div");
    File file = Screenshots.takeScreenShot(iframe, element);
    assertTrue(file == null);
  }

}
