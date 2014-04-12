package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.source;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class FramesTest extends IntegrationTest {
  @Before
  public void setUp() {
    openFile("page_with_frames.html");
  }

  @Test
  public void canSwitchBetweenFrames() {
    assumeFalse(isChrome());
    assertEquals("Test::frames", title());
//    System.out.println($("#top-frame"));
//    System.out.println($$(By.xpath("//frame")));

    switchTo().frame("topFrame");
    assertTrue(source().contains("Hello, WinRar!"));

    switchTo().defaultContent();
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));

    switchTo().defaultContent();
    switchTo().frame("mainFrame");
    $("h1").shouldHave(text("Page with JQuery"));
  }
}
