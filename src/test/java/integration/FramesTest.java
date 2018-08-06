package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchFrameException;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.Selenide.title;
import static com.codeborne.selenide.WebDriverRunner.currentFrameUrl;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.source;

class FramesTest extends IntegrationTest {
  @AfterAll
  static void tearDown() {
    close();
  }

  @BeforeEach
  void openPage() {
    openFile("page_with_frames.html");
  }

  @Test
  void canSwitchIntoInnerFrame() {
    assertThat(title())
      .isEqualTo("Test::frames");

    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
    assertThat(currentFrameUrl())
      .isEqualTo(Configuration.baseUrl + "/page_with_parent_frame.html");

    switchTo().innerFrame("parentFrame", "childFrame_1");
    assertThat(source())
      .contains("Hello, WinRar!");
    assertThat(currentFrameUrl())
      .isEqualTo(Configuration.baseUrl + "/hello_world.txt");

    switchTo().innerFrame("parentFrame", "childFrame_2");
    $("frame").shouldHave(name("childFrame_2_1"));
    assertThat(currentFrameUrl())
      .isEqualTo(Configuration.baseUrl + "/page_with_child_frame.html");

    switchTo().innerFrame("parentFrame", "childFrame_2", "childFrame_2_1");
    assertThat(source())
      .contains("This is last frame!");
    assertThat(currentFrameUrl())
      .isEqualTo(Configuration.baseUrl + "/child_frame.txt");

    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
    assertThat(currentFrameUrl())
      .isEqualTo(Configuration.baseUrl + "/page_with_parent_frame.html");
  }

  @Test
  void switchToInnerFrame_withoutParameters_switchesToDefaultContent() {
    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));

    switchTo().innerFrame();
    $("frame").shouldHave(name("topFrame"));
  }

  @Test
  void canSwitchBetweenFramesByTitle() {
    assertThat(title())
      .isEqualTo("Test::frames");

    switchTo().frame("topFrame");
    assertThat(source())
      .contains("Hello, WinRar!");

    switchTo().defaultContent();
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));

    switchTo().defaultContent();
    switchTo().frame("mainFrame");
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  void canSwitchBetweenFramesByIndex() {
    Assumptions.assumeFalse(isChrome());
    assertThat(title())
      .isEqualTo("Test::frames");

    switchTo().frame(0);
    assertThat(source())
      .contains("Hello, WinRar!");

    switchTo().defaultContent();
    switchTo().frame(1);
    $("h1").shouldHave(text("Page with dynamic select"));

    switchTo().defaultContent();
    switchTo().frame(2);
    $("h1").shouldHave(text("Page with JQuery"));
  }


  @Test
  void throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByElement() {
    assertThat(title())
      .isEqualTo("Test::frames");

    assertThatThrownBy(() -> {
      switchTo().frame("mainFrame");
      // $("#log") is present, but not frame.
      switchTo().frame($("#log"));
    }).isInstanceOf(NoSuchFrameException.class);
  }

  @Test
  void throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByTitle() {
    assertThat(title())
      .isEqualTo("Test::frames");
    assertThatThrownBy(() -> {
      switchTo().frame("absentFrame");
    }).isInstanceOf(NoSuchFrameException.class);
  }

  @Test
  void throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByIndex() {
    assertThat(title())
      .isEqualTo("Test::frames");

    assertThatThrownBy(() -> {
      switchTo().frame(Integer.MAX_VALUE);
    }).isInstanceOf(NoSuchFrameException.class);
  }
}
