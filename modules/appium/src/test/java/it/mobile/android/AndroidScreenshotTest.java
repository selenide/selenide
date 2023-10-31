package it.mobile.android;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import static com.codeborne.selenide.appium.SelenideAppium.$;
import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;

class AndroidScreenshotTest extends BaseApiDemosTest {
  private static final Logger log = LoggerFactory.getLogger(AndroidScreenshotTest.class);

  @Test
  void screenshot() throws MalformedURLException {
    String screenshot = Selenide.screenshot("android-screenshot");
    log.info("Screenshot of an element: {}", screenshot);
    assertThat(new File(new URL(screenshot).getFile())).exists();
  }

  @Test
  void screenshotAsBase64() throws IOException {
    String screenshotBase64 = Selenide.screenshot(OutputType.BASE64);
    log.info("Screenshot of an element: {}", screenshotBase64);
    byte[] imageBytes = Base64.getDecoder().decode(screenshotBase64);
    BufferedImage screenshot = ImageIO.read(new ByteArrayInputStream(imageBytes));
    logScreenshot(screenshot);
    assertThat(screenshot.getWidth()).isGreaterThan(100);
  }

  @Test
  void elementScreenshot() {
    File screenshot = $(AppiumBy.xpath(".//*[@text='Views']")).screenshot();
    logScreenshot(screenshot);
    assertThat(screenshot).exists();
  }

  @Test
  void elementScreenshotAsImage() throws IOException {
    BufferedImage screenshot = $(AppiumBy.xpath(".//*[@text='Views']")).screenshotAsImage();
    logScreenshot(screenshot);
    assertThat(screenshot.getWidth()).isGreaterThan(100);
  }

  private static void logScreenshot(BufferedImage screenshot) throws IOException {
    File imageFile = new File(Configuration.reportsFolder, currentTimeMillis() + "." + Math.random() + ".png");
    ImageIO.write(screenshot, "png", imageFile);
    logScreenshot(imageFile);
  }

  private static void logScreenshot(File imageFile) {
    log.info("Screenshot of an element: {}", imageFile.getAbsolutePath());
  }
}
