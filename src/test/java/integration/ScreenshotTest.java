package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static integration.SelenideMethodsTest.assertBetween;
import static org.junit.Assume.assumeFalse;

public class ScreenshotTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    assumeFalse(isHtmlUnit());
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void canTakeScreenshotOfElement() throws IOException {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));
    File screenshot = select.screenshot();
    String info = "(Screenshot of element: " + screenshot.getAbsolutePath() + ") ";
    
    BufferedImage img = ImageIO.read(screenshot);
    assertBetween("Screenshot doesn't fit width " + info, img.getWidth(), 50, 200);
    assertBetween("Screenshot doesn't fit height " + info, img.getHeight(), 10, 30);
  }
}
