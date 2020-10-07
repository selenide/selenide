package integration;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.OutputType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

final class ScreenshotsTest extends IntegrationTest {
  @RegisterExtension static ScreenShooterExtension screenshotPerTest = new ScreenShooterExtension();
  private final ScreenShotLaboratory screenshots = ScreenShotLaboratory.getInstance();

  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
    assertThat(screenshots.getContextScreenshots()).isEmpty();
  }

  @Test
  void canTakeScreenshotInBase64Format() throws IOException {
    String screenshot = Selenide.screenshot(OutputType.BASE64);
    byte[] decoded = Base64.getDecoder().decode(screenshot);
    BufferedImage img = ImageIO.read(new ByteArrayInputStream(decoded));
    assertThat(img.getWidth()).isBetween(50, 3000);
    assertThat(img.getHeight()).isBetween(50, 3000);

    assertThat(screenshots.getContextScreenshots())
      .as("Base64 screenshots are not added to history")
      .isEmpty();
  }

  @Test
  void canTakeScreenshotAsTemporaryFile() throws IOException {
    File screenshot = Selenide.screenshot(OutputType.FILE);
    BufferedImage img = ImageIO.read(screenshot);
    assertThat(img.getWidth()).isBetween(50, 3000);
    assertThat(img.getHeight()).isBetween(50, 3000);

    assertThat(screenshots.getContextScreenshots())
      .as("Temporary files screenshots are added to history")
      .hasSize(1);
    assertThat(screenshots.getContextScreenshots().get(0)).isSameAs(screenshot);
  }
}
