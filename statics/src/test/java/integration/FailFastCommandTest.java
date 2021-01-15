package integration;

import com.codeborne.selenide.FailFastCondition;
import com.codeborne.selenide.ex.FailFastException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FailFastCommandTest extends IntegrationTest {

  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_error_with_delay.html");
  }

  @Test
  @Timeout(2000)
  void shouldFailFast() {
    FailFastCondition errorDetected = new FailFastCondition(() -> $("#error").exists(),
      "error text detected!");

    assertThrows(FailFastException.class, () ->
      $("input").failIf(errorDetected).shouldBe(visible, Duration.ofSeconds(5)));
  }

}
