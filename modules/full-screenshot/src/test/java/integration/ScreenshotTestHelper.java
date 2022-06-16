package integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ScreenshotTestHelper {
  private static final Logger log = LoggerFactory.getLogger(ScreenshotTestHelper.class);

  static void verifyScreenshotSize(File screenshot, int width, int height) throws IOException {
    BufferedImage img = ImageIO.read(screenshot);
    log.info("Verify screenshot {} of size {}x{}", screenshot.getAbsolutePath(), img.getWidth(), img.getHeight());
    if (nearlyEqual(img.getWidth(), width * 2) && nearlyEqual(img.getHeight(), height * 2)) {
      // it's Retina display, it has 2x more pixels
      log.info("Screenshot matches {}x{} on Retina display", width, height);
    }
    else {
      assertThat(img.getWidth()).isBetween(width - 50, width + 50);
      assertThat(img.getHeight()).isBetween(height - 50, height + 50);
    }
  }

  private static boolean nearlyEqual(int actual, int expected) {
    return actual > expected - 50 && actual < expected + 50;
  }
}
