package integration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;

public class FullScreenshotsGridTest extends AbstractGridTest {

  @BeforeEach
  void setUp() {
    Configuration.remote = "http://localhost:" + hubPort + "/wd/hub";
  }

  @AfterEach
  void tearDown() {
    Configuration.remote = null;
  }

  /*
   It fails in non-local Chromium.
   Probably it failed because here
   https://github.com/SeleniumHQ/selenium/blob/trunk/java/src/org/openqa/selenium/devtools/DevTools.java#L99
   wrong targedId detected and devtools sends Page.captureScreenshot to an inactive page that leads TimeoutException.
   */
  @Test
  void canTakeFullScreenshotWithTwoTabs() throws IOException {
    openFile("page_with_selects_without_jquery.html");
    $("h1").shouldHave(text("Page with selects"));

    Selenide.executeJavaScript("window.open()");
    Selenide.switchTo().window(1);
    openFile("file_upload_form.html");
    Selenide.switchTo().window(0);

    File screenshot = Selenide.screenshot(OutputType.FILE);
    BufferedImage img = ImageIO.read(screenshot);

    assertThat(img.getWidth()).isBetween(2200, 3000);
    assertThat(img.getHeight()).isBetween(2400, 3000);
  }
}
