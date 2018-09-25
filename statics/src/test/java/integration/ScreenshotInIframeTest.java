package integration;

import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class ScreenshotInIframeTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeFalse(isHtmlUnit() || isPhantomjs());
    openFile("page_with_iframe.html");
  }

  @Test
  void canTakeScreenshotAsImageOfFullyVisibleElementInIframe() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#small_div");
    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);
    switchTo().frame(iframe);
    assertThat(image.getWidth())
      .isEqualTo(element.getSize().getWidth());
    assertThat(image.getHeight())
      .isEqualTo(element.getSize().getHeight());
    switchTo().defaultContent();
  }

  @Test
  void canTakeScreenshotOfFullyVisibleElementInIframe() throws Exception {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#small_div");
    File file = Screenshots.takeScreenShot(iframe, element);
    BufferedImage image = ImageIO.read(file);
    switchTo().frame(iframe);
    assertThat(image.getWidth())
      .isEqualTo(element.getSize().getWidth());
    assertThat(image.getHeight())
      .isEqualTo(element.getSize().getHeight());
    switchTo().defaultContent();
  }

  @Test
  void canTakeScreenshotAsImageOfPartiallyVisibleElementInIframe() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#wide_div");
    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);
    switchTo().frame(iframe);
    assertThat(image.getWidth())
      .isNotEqualTo(element.getSize().getWidth());
    assertThat(image.getHeight())
      .isEqualTo(element.getSize().getHeight());
    switchTo().defaultContent();
  }

  @Test
  void canTakeScreenshotOfPartiallyVisibleElementInIframe() throws Exception {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#wide_div");
    File file = Screenshots.takeScreenShot(iframe, element);
    BufferedImage image = ImageIO.read(file);
    switchTo().frame(iframe);
    assertThat(image.getWidth())
      .isNotEqualTo(element.getSize().getWidth());
    assertThat(image.getHeight())
      .isEqualTo(element.getSize().getHeight());
    switchTo().defaultContent();
  }

  @Test
  void screenshotAsImageOfNotVisibleElementIsNotTaken() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#big_div");
    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);
    assertThat(image)
      .isNull();
  }

  @Test
  void screenshotOfNotVisibleElementIsNotTaken() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#big_div");
    File file = Screenshots.takeScreenShot(iframe, element);
    assertThat(file)
      .isNull();
  }
}
