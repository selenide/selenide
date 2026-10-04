package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.UIAssertionError;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.HoverOptions.withOffset;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

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
  void shouldNotReportElementNotFound_inCaseOfOtherExceptions() {
    Configuration.timeout = 300;
    assertThatThrownBy(() ->
      $("[name=username]").hover(withOffset(1_000_000, 1_000_000)))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageContaining("MoveTargetOutOfBoundsException: move target out of bounds")
      .hasMessageContaining("Timeout: 300ms");
  }
}
