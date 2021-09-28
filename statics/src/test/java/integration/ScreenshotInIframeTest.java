package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static integration.ImageTestHelper.BLUE;
import static integration.ImageTestHelper.GRAY;
import static integration.ImageTestHelper.PINK;
import static integration.ImageTestHelper.RED;
import static integration.ImageTestHelper.assertBody;
import static integration.ImageTestHelper.assertBorder;
import static integration.ImageTestHelper.assertBottomBody;
import static integration.ImageTestHelper.assertBottomBorder;
import static integration.ImageTestHelper.assertLeftBody;
import static integration.ImageTestHelper.assertLeftBorder;
import static integration.ImageTestHelper.assertTopBody;
import static integration.ImageTestHelper.assertTopBorder;
import static org.assertj.core.api.Assertions.assertThat;

final class ScreenshotInIframeTest extends IntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(ScreenshotInIframeTest.class);

  @BeforeEach
  void setUp() {
    openFile("page_with_iframe.html");
  }

  @Test
  void canTakeScreenshotAsImageOfFullyVisibleElementInIframe() throws IOException {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#small_div");

    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);

    describe(image);
    assertBorder(image, RED);
    assertBody(image, BLUE);
  }

  @Test
  void canTakeScreenshotOfFullyVisibleElementInIframe() throws IOException {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#small_div");

    File file = Screenshots.takeScreenShot(iframe, element);

    assertThat(file).isNotNull();
    log.info("Element screenshot: {}", file);
    assertBorder(ImageIO.read(file), RED);
    assertBody(ImageIO.read(file), BLUE);
  }

  @Test
  void canTakeScreenshotAsImageOfPartiallyVisibleElementInIframe() {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#wide_div");

    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);

    assertThat(image).isNotNull();
    assertTopBorder(image, RED, 100);
    assertBottomBorder(image, RED, 100);
    assertLeftBorder(image, RED);

    assertTopBody(image, GRAY, 100);
    assertBottomBody(image, GRAY, 100);
    assertLeftBody(image, GRAY);
  }

  @Test
  void canTakeScreenshotOfPartiallyVisibleElementInIframe() throws IOException {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#wide_div");

    File file = Screenshots.takeScreenShot(iframe, element);

    assertThat(file).isNotNull();
    BufferedImage image = ImageIO.read(file);
    log.info("Check element screenshot: {}", file);

    assertTopBorder(image, RED, 100);
    assertBottomBorder(image, RED, 100);
    assertLeftBorder(image, RED);

    assertTopBody(image, GRAY, 100);
    assertBottomBody(image, GRAY, 100);
    assertLeftBody(image, GRAY);
  }

  @Test
  void screenshotAsImageOfNotVisibleElementIsNotTaken() throws IOException {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#big_div");

    BufferedImage image = Screenshots.takeScreenShotAsImage(iframe, element);

    if (browser().isFirefox()) {
      assertThat(image).isNotNull();
      describe(image);
      assertBorder(image, RED);
      assertBody(image, PINK);
    }
    else {
      assertThat(image).isNull();
    }
  }

  @Test
  void screenshotOfNotVisibleElementIsNotTaken() throws IOException {
    SelenideElement iframe = $("#iframe_page");
    SelenideElement element = $("#big_div");

    File file = Screenshots.takeScreenShot(iframe, element);

    if (browser().isFirefox()) {
      assertThat(file).isNotNull();
      log.info("Element screenshot: {}", file);
      assertBorder(ImageIO.read(file), RED);
      assertBody(ImageIO.read(file), PINK);
    }
    else {
      assertThat(file).isNull();
    }
  }

  private static void describe(BufferedImage image) throws IOException {
    File imageFile = new File(Configuration.reportsFolder, System.currentTimeMillis() + ".png");
    ImageIO.write(image, "png", imageFile);
    log.info("Element screenshot: {}", imageFile);
  }
}
