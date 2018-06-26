package integration;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;

class ScreenshotInIframeTest extends IntegrationTest {

  @BeforeEach
  void setUp() {
    Assumptions.assumeFalse(isHtmlUnit());
    openFile("page_with_iframe.html");
  }

  @Test
  void canTakeScreenshotAsImageOfFullyVisibleElementInIframe() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#small_div");
    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);
    switchTo().frame(iframe);
    Assertions.assertEquals(element.getSize().getWidth(), image.getWidth());
    Assertions.assertEquals(element.getSize().getHeight(), image.getHeight());
    switchTo().defaultContent();
  }

  @Test
  void canTakeScreenshotOfFullyVisibleElementInIframe() throws Exception {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#small_div");
    File file = Screenshots.takeScreenShot(iframe, element);
    BufferedImage image = ImageIO.read(file);
    switchTo().frame(iframe);
    Assertions.assertEquals(element.getSize().getWidth(), image.getWidth());
    Assertions.assertEquals(element.getSize().getHeight(), image.getHeight());
    switchTo().defaultContent();
  }

  @Test
  void canTakeScreenshotAsImageOfPartiallyVisibleElementInIframe() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#wide_div");
    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);
    switchTo().frame(iframe);
    Assertions.assertNotEquals(element.getSize().getWidth(), image.getWidth());
    Assertions.assertEquals(element.getSize().getHeight(), image.getHeight());
    switchTo().defaultContent();
  }

  @Test
  void canTakeScreenshotOfPartiallyVisibleElementInIframe() throws Exception {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#wide_div");
    File file = Screenshots.takeScreenShot(iframe, element);
    BufferedImage image = ImageIO.read(file);
    switchTo().frame(iframe);
    Assertions.assertNotEquals(element.getSize().getWidth(), image.getWidth());
    Assertions.assertEquals(element.getSize().getHeight(), image.getHeight());
    switchTo().defaultContent();
  }

  @Test
  void screenshotAsImageOfNotVisibleElementIsNotTaken() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#big_div");
    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);
    Assertions.assertTrue(image == null);
  }

  @Test
  void screenshotOfNotVisibleElementIsNotTaken() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#big_div");
    File file = Screenshots.takeScreenShot(iframe, element);
    Assertions.assertTrue(file == null);
  }
}
