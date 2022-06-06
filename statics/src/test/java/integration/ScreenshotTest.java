package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static com.codeborne.selenide.Selenide.$;
import static integration.ImageTestHelper.BLUE;
import static integration.ImageTestHelper.RED;
import static integration.ImageTestHelper.assertBody;
import static integration.ImageTestHelper.assertBorder;
import static org.assertj.core.api.Assertions.assertThat;

final class ScreenshotTest extends IntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(ScreenshotTest.class);

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

    String screenshotFolder = screenshotFolder();
    assertThat(screenshot.getPath())
      .as(String.format("Screenshot file should be located in %s, but was: %s", screenshotFolder, screenshot.getPath()))
      .startsWith(screenshotFolder);

    BufferedImage img = ImageIO.read(screenshot);

    assertThat(element.getSize().getWidth()).isBetween(img.getWidth() / 2, img.getWidth());
    assertThat(element.getSize().getHeight()).isBetween(img.getHeight() / 2, img.getHeight());

    assertBorder(img, RED);
    assertBody(img, BLUE);
  }

  private String screenshotFolder() {
    return Paths.get(Configuration.reportsFolder)
      .toAbsolutePath()
      .toString();
  }
}
