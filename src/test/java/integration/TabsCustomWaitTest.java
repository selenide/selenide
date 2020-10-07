package integration;

import com.codeborne.selenide.ex.WindowNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class TabsCustomWaitTest extends ITest {
  @BeforeEach
  void setUp() {
    openFile("page_with_tabs_with_big_delays.html");
  }

  @Test
  void waitsUntilTabAppears_withCustomTimeout() {
    setTimeout(1000);
    $("#open-new-tab-with-delay").click();
    switchTo().window("Test::alerts", Duration.ofSeconds(3));
    $("h1").shouldHave(text("Page with alerts"));
  }

  @Test
  void waitsUntilTabAppears_withoutCustomTimeout() {
    $("#open-new-tab-with-delay").click();
    assertThatThrownBy(() -> switchTo().window(1))
      .isInstanceOf(WindowNotFoundException.class);
  }

  @Test
  void waitsUntilTabAppears_withLowerTimeout() {
    $("#open-new-tab-with-delay").click();
    assertThatThrownBy(() -> switchTo().window(1, Duration.ofSeconds(1)))
      .isInstanceOf(WindowNotFoundException.class);
  }

  @AfterEach
  void tearDown() {
    driver().close();
  }
}
