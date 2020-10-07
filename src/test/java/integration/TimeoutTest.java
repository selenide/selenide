package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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
    } catch (NoSuchElementException expectedException) {
      long end = System.nanoTime();
      assertThat(end - start < 1200000000L)
        .withFailMessage("Looking for non-existing element took more than 1.2 ms: " + (end - start) / 1000000 + " ms.")
        .isTrue();
    }
  }

  @Test
  void timeoutShouldBeInMilliseconds() {
    try {
      $(By.xpath("//h16")).waitUntil(visible, 15);
    } catch (ElementNotFound expectedException) {
      assertThat(expectedException.toString())
        .withFailMessage(String.format("Error message should contain timeout '15 ms', but received: %s", expectedException.toString()))
        .contains("15 ms");
    }
  }

  @Test
  void timeoutShouldBeFormattedInErrorMessage() {
    try {
      $(By.xpath("//h19")).waitUntil(visible, 1500);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expectedException) {
      assertThat(expectedException.toString())
        .withFailMessage(String.format("Error message should contain timeout '1.500 s', but received: %s",
          expectedException.toString()))
        .contains("1.500 s");
    }
  }

  @Test
  void timeoutLessThanSecond() {
    try {
      $(By.xpath("//h18")).waitUntil(visible, 800);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expectedException) {

      assertThat(expectedException.toString())
        .withFailMessage(String.format("Error message should contain timeout '800 ms', but received: %s", expectedException.toString()))
        .contains("800 ms");
    }
  }
}
