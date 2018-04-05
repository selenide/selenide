package integration;

import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class ScreenshotInIframeTest extends IntegrationTest {

  @Before
  public void setUp() {
    assumeFalse(isHtmlUnit());
    openFile("page_with_iframe.html");
  }

  @Test
  public void canTakeScreenshotOfFullyVisibleElementInIframe() {
    SelenideElement element = $("#small_div");
    SelenideElement iframe = $("#iframe_page");
    BufferedImage image = Screenshots.takeScreenShotAsImage(element, iframe);
    switchTo().frame(iframe);
    assertEquals(element.getSize().getWidth(), image.getWidth());
    assertEquals(element.getSize().getHeight(), image.getHeight());
    switchTo().defaultContent();
  }

  @Test
  public void canTakeScreenshotOfPartiallyVisibleElementInIframe() {
    SelenideElement element = $("#wide_div");
    SelenideElement iframe = $("#iframe_page");
    BufferedImage image = Screenshots.takeScreenShotAsImage(element, iframe);
    switchTo().frame(iframe);
    assertNotEquals(element.getSize().getWidth(), image.getWidth());
    assertEquals(element.getSize().getHeight(), image.getHeight());
    switchTo().defaultContent();
  }

  @Test
  public void sceenshotOfNotVisibleElementIsNotTaken() {
    SelenideElement element = $("#big_div");
    SelenideElement iframe = $("#iframe_page");
    BufferedImage image = Screenshots.takeScreenShotAsImage(element, iframe);
    assertTrue(image == null);
  }

}
