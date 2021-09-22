package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

final class ScreenshotTest extends IntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(ScreenshotTest.class);

  private static final Color RED = new Color(255, 0, 0);
  private static final Color BLUE = new Color(0, 255, 255);

  @BeforeEach
  void openTestPage() {
    openFile("page_with_big_divs.html");
  }

  @Test
  void canTakeScreenshotOfElement() throws IOException {
    SelenideElement element = $("#small_div");
    File screenshot = element.screenshot();
    assertThat(screenshot).isNotNull();
    log.info("Analyze the element screenshot {}", screenshot.getAbsolutePath());

    String screenshotFolder = new File(System.getProperty("user.dir"), Configuration.reportsFolder).getCanonicalPath();
    assertThat(screenshot.getPath())
      .as(String.format("Screenshot file should be located in %s, but was: %s", screenshotFolder, screenshot.getPath()))
      .startsWith(screenshotFolder);

    BufferedImage img = ImageIO.read(screenshot);

    assertThat(element.getSize().getWidth()).isBetween(img.getWidth() / 2, img.getWidth());
    assertThat(element.getSize().getHeight()).isBetween(img.getHeight() / 2, img.getHeight());

    assertColor(img, "top left corner", 1, 1, RED);
    assertColor(img, "bottom left corner", 1, img.getHeight() - 1, RED);
    assertColor(img, "top right corner", img.getWidth() - 1, 1, RED);
    assertColor(img, "bottom right corner", img.getWidth() - 1, img.getHeight() - 1, RED);
    assertColor(img, "top left inside of border", 11, 11, BLUE);
    assertColor(img, "bottom left inside of border", 11, img.getHeight() - 11, BLUE);
    assertColor(img, "top right inside of border", img.getWidth() - 11, 11, BLUE);
    assertColor(img, "bottom right inside of border", img.getWidth() - 11, img.getHeight() - 11, BLUE);
  }

  private void assertColor(BufferedImage img, String description, int x, int y, Color expectedColor) {
    assertThat(new Color(img.getRGB(x, y))).as(description).isEqualTo(expectedColor);
  }
}
