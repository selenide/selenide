package integration;

import com.automation.remarks.video.annotations.Video;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.Selenide.switchTo;
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

  @Test @Video
  public void waitsUntilFrameAppears_inner() {
    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
  }

  @Test @Video
  public void waitsUntilFrameAppears_byTitle() {
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));
  }

  @Test @Video
  public void waitsUntilFrameAppears_byIndex() {
    assumeFalse(isHtmlUnit());

    switchTo().frame(2);
    sleep(100);
    assertThat(source(), containsString("Page with JQuery"));
  }
}
