package integration;

import com.codeborne.selenide.Configuration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ScreenshotTestHelper {
  private static final Logger log = LoggerFactory.getLogger(ScreenshotTestHelper.class);

  static void verifyScreenshotSize(File screenshot, int width, int height) throws IOException {
    BufferedImage img = ImageIO.read(screenshot);
    log.info("Verify screenshot {} of size {}x{}", screenshot.getAbsolutePath(), img.getWidth(), img.getHeight());
    if (nearlyEqual(img.getWidth(), width * 2) && nearlyEqual(img.getHeight(), height * 2)) {
      // it's Retina display, it has 2x more pixels
      log.info("Screenshot matches {}x{} size on Retina display", width, height);
    }
    else if (nearlyEqual(img.getWidth(), width) && nearlyEqual(img.getHeight(), height)) {
      log.info("Screenshot matches {}x{} size", width, height);
    }
    else {
      File archivedFile = new File(Configuration.reportsFolder, UUID.randomUUID() + ".png");
      FileUtils.copyFile(screenshot, archivedFile);
      log.info("Screenshot does not match {}x{} size: {}", width, height, archivedFile.getAbsolutePath());
      throw new AssertionError(String.format("Screenshot %s is expected to have size %sx%s, but actual size: %sx%s",
        archivedFile.getAbsolutePath(), width, height, img.getWidth(), img.getHeight()));
    }
  }

  private static boolean nearlyEqual(int actual, int expected) {
    return actual > expected - 50 && actual < expected + 50;
  }
}
