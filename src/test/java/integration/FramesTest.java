package integration;

import com.codeborne.selenide.Configuration;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.Selenide.title;
import static com.codeborne.selenide.WebDriverRunner.currentFrameUrl;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.source;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeFalse;

public class FramesTest extends IntegrationTest {
  @Before
  public void openPage() {
    openFile("page_with_frames.html");
  }

  @Test
  public void canSwitchIntoInnerFrame() {
    assertEquals("Test::frames", title());

    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_parent_frame.html"));

    switchTo().innerFrame("parentFrame", "childFrame_1");
    assertThat(source(), containsString("Hello, WinRar!"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/hello_world.txt"));

    switchTo().innerFrame("parentFrame", "childFrame_2");
    $("frame").shouldHave(name("childFrame_2_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_child_frame.html"));

    switchTo().innerFrame("parentFrame", "childFrame_2", "childFrame_2_1");
    assertThat(source(), containsString("This is last frame!"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/child_frame.txt"));

    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
    assertThat(currentFrameUrl(), equalTo(Configuration.baseUrl + "/page_with_parent_frame.html"));
  }

  @Test
  public void switchToInnerFrame_withoutParameters_switchesToDefaultContent() {
    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));

    switchTo().innerFrame();
    $("frame").shouldHave(name("topFrame"));
  }

  @Test
  public void canSwitchBetweenFramesByTitle() {
    assertEquals("Test::frames", title());

    switchTo().frame("topFrame");
    assertThat(source(), containsString("Hello, WinRar!"));

    switchTo().defaultContent();
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));

    switchTo().defaultContent();
    switchTo().frame("mainFrame");
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  public void canSwitchBetweenFramesByIndex() {
    assumeFalse(isChrome());
    assertEquals("Test::frames", title());

    switchTo().frame(0);
    assertThat(source(), containsString("Hello, WinRar!"));

    switchTo().defaultContent();
    switchTo().frame(1);
    $("h1").shouldHave(text("Page with dynamic select"));

    switchTo().defaultContent();
    switchTo().frame(2);
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @AfterClass
  public static void tearDown() {
    close();
  }
}
