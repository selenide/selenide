package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.source;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeFalse;

public class FrameWaitTest extends IntegrationTest {
  @Before
  public void setUp() {
    openFile("page_with_frames_with_delays.html");
  }

  @Test
  public void waitsUntilFrameAppears_inner() {
    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
  }
  
  @Test
  public void waitsUntilFrameAppears_byTitle() {
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));
  }

  @Test
  public void waitsUntilFrameAppears_byIndex() {
    assumeFalse(isChrome() || isHtmlUnit());

    switchTo().frame(2);
//    assertThat(source(), containsString(isFirefox() || isChrome() ? "Hello, WinRar!" : "This is last frame!"));
    assertThat(source(), containsString("Page with JQuery"));
  }
}
