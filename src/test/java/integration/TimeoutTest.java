package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.visible;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.openqa.selenium.By.xpath;

final class TimeoutTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void lookingForNonExistingElementShouldFailFast() {
    long start = System.nanoTime();
    try {
      driver().getWebDriver().findElement(By.id("nonExistingElement"));
      fail("Looking for non-existing element should fail");
    }
    catch (NoSuchElementException expectedException) {
      long end = System.nanoTime();
      assertThat(end - start)
        .withFailMessage("Looking for non-existing element took more than 1.2 ms: " + NANOSECONDS.toMillis(end - start) + " ms.")
        .isLessThan(MILLISECONDS.toNanos(1200));
    }
  }

  @Test
  void timeoutShouldBeInMilliseconds() {
    assertThatThrownBy(() -> $(xpath("//h16")).shouldBe(visible, ofMillis(15)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("15 ms");
  }

  @Test
  void timeoutShouldBeFormattedInErrorMessage() {
    assertThatThrownBy(() -> $(xpath("//h19")).shouldBe(visible, ofMillis(1500)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("1.500 s");
  }

  @Test
  void timeoutLessThanSecond() {
    assertThatThrownBy(() -> $(xpath("//h18")).shouldBe(visible, ofMillis(800)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("800 ms");
  }
}
