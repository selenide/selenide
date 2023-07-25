package integration;

import com.codeborne.selenide.ex.FrameNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.WebDriverConditions.currentFrameUrl;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

final class FramesTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_frames.html");
  }

  @Test
  void canSwitchIntoInnerFrame() {
    assertThat(driver().title()).isEqualTo("Test::frames");

    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
    driver().webdriver().shouldHave(currentFrameUrl(getBaseUrl() + "/page_with_parent_frame.html"));

    switchTo().innerFrame("parentFrame", "childFrame_1");
    assertThat(driver().source()).contains("Hello, WinRar!");
    driver().webdriver().shouldHave(currentFrameUrl(getBaseUrl() + "/hello_world.txt"));
    driver().webdriver().shouldHave(urlContaining("/page_with_frames.html"));

    switchTo().innerFrame("parentFrame", "childFrame_2");
    $("frame").shouldHave(name("childFrame_2_1"));
    driver().webdriver().shouldHave(currentFrameUrl(getBaseUrl() + "/page_with_child_frame.html"));
    driver().webdriver().shouldHave(urlContaining("/page_with_frames.html"));

    switchTo().innerFrame("parentFrame", "childFrame_2", "childFrame_2_1");
    assertThat(driver().source()).contains("This is last frame!");
    driver().webdriver().shouldHave(currentFrameUrl(getBaseUrl() + "/child_frame.txt"));
    driver().webdriver().shouldHave(urlContaining("/page_with_frames.html"));

    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
    driver().webdriver().shouldHave(urlContaining("/page_with_frames.html"));
    assertThat(driver().getCurrentFrameUrl()).isEqualTo(getBaseUrl() + "/page_with_parent_frame.html");
  }

  @Test
  void canSwitchBetweenFramesByTitle() {
    assertThat(driver().title()).isEqualTo("Test::frames");

    switchTo().frame("topFrame");
    assertThat(driver().source()).contains("Hello, WinRar!");

    switchTo().defaultContent();
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));

    switchTo().defaultContent();
    switchTo().frame("mainFrame");
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  void canSwitchBetweenFramesByIndex() {
    assumeThat(browser().isChrome()).isFalse();
    assertThat(driver().title()).isEqualTo("Test::frames");

    switchTo().frame(0);
    assertThat(driver().source()).contains("Hello, WinRar!");

    switchTo().defaultContent();
    switchTo().frame(1);
    $("h1").shouldHave(text("Page with dynamic select"));

    switchTo().defaultContent();
    switchTo().frame(2);
    $("h1").shouldHave(text("Page with JQuery"));
  }


  @Test
  void throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByElement() {
    assertThat(driver().title()).isEqualTo("Test::frames");

    assertThatThrownBy(() -> {
      switchTo().frame("mainFrame");
      // $("#log") is present, but not frame.
      switchTo().frame($("#log"));
    })
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with element: {#log}");
  }

  @Test
  void throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByTitle() {
    assertThat(driver().title()).isEqualTo("Test::frames");
    assertThatThrownBy(() -> switchTo().frame("absentFrame"))
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with id/name: absentFrame");
  }

  @Test
  void throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByIndex() {
    assertThat(driver().title()).isEqualTo("Test::frames");

    assertThatThrownBy(() -> switchTo().frame(Integer.MAX_VALUE))
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with index: " + Integer.MAX_VALUE);
  }

  @Test
  void attachesScreenshotWhenCannotFrameNotFound() {
    assertThatThrownBy(() -> switchTo().frame(33))
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with index: 33")
      .hasMessageContaining("Screenshot: file:")
      .hasMessageContaining("Page source: file:")
      .hasMessageContaining("Caused by: TimeoutException:");
  }
}
