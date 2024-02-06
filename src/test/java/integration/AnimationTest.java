package integration;

import com.codeborne.selenide.WebDriverConditions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.animated;
import static com.codeborne.selenide.Selectors.byText;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class AnimationTest extends ITest {
  @BeforeEach
  void openAnimationTestPage() {
    openFile("animations.html");
  }

  @AfterEach
  void tearDown() {
    driver().close();
  }

  @Test
  void shouldCheckNotAnimatedBox() {
    $(".magic-box").shouldNotBe(animated);
  }

  @Test
  void shouldCheckAnimatedBox() {
    $("#move-box").click();
    $(".magic-box").shouldBe(animated);
  }

  @Test
  void shouldFailIfTabIsNotActive() throws InterruptedException {
    $(byText("New tab")).click();
    $("#move-box").click();
    driver().webdriver().shouldHave(WebDriverConditions.numberOfWindows(2));
    if (driver().browser().isFirefox()) {
      // for some reason Firefox is not immediately stop calling requestAnimationFrame(callback)
      pause(2000);
    }
    assertThatThrownBy(() -> $(".magic-box").shouldBe(animated))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("You are checking for animations on an inactive(background) tab. " +
        "It is impossible to check for animations on inactive tab.");
  }

  static void pause(long milliseconds) {
    try {
      sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
