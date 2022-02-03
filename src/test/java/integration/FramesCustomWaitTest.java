package integration;

import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.codeborne.selenide.ex.FrameNotFoundException;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class FramesCustomWaitTest extends ITest {

  @BeforeEach
  void setUp() {
    openFile("page_with_frames_with_big_delays.html");
  }

  @Test
  void waitsUntilFrameAppears_withCustomTimeout() {
    setTimeout(1000);
    $("#btn").click();
    switchTo().frame("ifrm", Duration.ofSeconds(6));
    $("body").shouldBe(text("This is last frame!"));
  }

  @Test
  void waitsUntilFrameAppears_withoutCustomTimeout() {
    $("#btn").click();
    assertThatThrownBy(() -> switchTo().frame(1))
      .isInstanceOf(FrameNotFoundException.class);
  }

  @Test
  void waitsUntilFrameAppears_withLowerTimeout() {
    $("#btn").click();
    assertThatThrownBy(() -> switchTo().frame(1, Duration.ofSeconds(1)))
      .isInstanceOf(FrameNotFoundException.class);
  }

}
