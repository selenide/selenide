package integration;

import com.automation.remarks.video.annotations.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WindowType;

import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FrameWaitTest extends ITest {
  @BeforeEach
  void setUp() {
    openFile("page_with_frames_with_delays.html");
  }

  @Test
  @Video
  void waitsUntilFrameAppears_inner() {
    switchTo().innerFrame("parentFrame");
    $("frame").shouldHave(name("childFrame_1"));
  }

  @Test
  @Video
  void waitsUntilFrameAppears_byTitle() {
    switchTo().frame("leftFrame");
    $("h1").shouldHave(text("Page with dynamic select"));
  }

  @Test
  @Video
  void waitsUntilFrameAppears_byIndex() {
    switchTo().frame(2);

    $("h1").shouldHave(text("Page with JQuery"));
    assertThat(driver().source()).contains("Test::jquery");
  }

  @Test
  @Video
  void opensNewTab() {
    assumeTrue(browser().isFirefox() || browser().isIE());
    assertThat(driver().driver().getWebDriver().getWindowHandles()).hasSize(1);

    switchTo().newWindow(WindowType.TAB);

    assertThat(driver().driver().getWebDriver().getWindowHandles()).hasSize(2);
    openFile("page_with_images.html");
    assertThat($("#valid-image img").isImage()).isTrue();

    driver().driver().getWebDriver().close();
    assertThat(driver().driver().getWebDriver().getWindowHandles()).hasSize(1);
  }
}
