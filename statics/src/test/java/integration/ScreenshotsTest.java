package integration;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemOut;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.stream.output.OutputFactories.tapAndOutput;

@ExtendWith(SystemStubsExtension.class)
final class ScreenshotsTest extends IntegrationTest {
  private static final ScreenShotLaboratory screenshots = inject();

  @SystemStub
  SystemOut systemOut = new SystemOut(tapAndOutput());

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
    BufferedImage img = ImageIO.read(requireNonNull(screenshot));
    assertThat(img.getWidth()).isBetween(50, 3000);
    assertThat(img.getHeight()).isBetween(50, 3000);
    assertThat(screenshot).exists();

    assertThat(screenshots.getContextScreenshots())
      .as("Temporary files screenshots are added to history")
      .hasSize(1);
    assertThat(screenshots.getContextScreenshots().get(0)).isEqualTo(screenshot);
  }

  @Test
  void canTakeScreenshotAtEveryMoment() throws URISyntaxException {
    String fileName = "screenshot-" + randomUUID();
    String screenshot = Selenide.screenshot(fileName);

    assertThat(screenshot).startsWith("file:/");
    assertThat(screenshot).endsWith(".png");
    assertThatFileExistsAndIsReferencedInSystemOut(screenshot);

    String pageSource = screenshot.replace(".png", ".html");
    assertThatFileExistsAndIsReferencedInSystemOut(pageSource);
  }

  private void assertThatFileExistsAndIsReferencedInSystemOut(String url) throws URISyntaxException {
    File file = new File(new URI(url));
    assertThat(file).exists();
    assertThat(systemOut.getLines()).containsOnlyOnce("[[ATTACHMENT|%s]]".formatted(file));
  }
}
