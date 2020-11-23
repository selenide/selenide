package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.UIAssertionError;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

@Disabled
final class MethodCalledOnElementWithInvalidOperationFailsOnTest extends IntegrationTest {
  @BeforeEach
  void openPage() {
    assumeThat(isChrome()).isTrue();
    givenHtml(
      "<form>Type username:",
      "<input name='username'></input>",
      "</form>"
    );
  }

  @Test
  void shouldNotReportElementNotFoundException() {
    Configuration.timeout = 300;
    assertThatThrownBy(() ->
      $("[name=username]").sendKeys("\uD83D\uDE06"))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageContaining("WebDriverException: unknown error: ChromeDriver only supports characters in the BMP")
      .hasMessageContaining("Timeout: 300 ms.");
  }
}
