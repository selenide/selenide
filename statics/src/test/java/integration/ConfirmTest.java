package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.DialogTextMismatch;
import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.ModalOptions.withExpectedText;
import static com.codeborne.selenide.ModalOptions.withTimeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.confirm;
import static com.codeborne.selenide.Selenide.dismiss;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.openqa.selenium.UnexpectedAlertBehaviour.IGNORE;
import static org.openqa.selenium.remote.CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR;

final class ConfirmTest extends IntegrationTest {
  private static final String USER_NAME = "John Mc'Clane";
  private static final String ALERT_TEXT = "Get out of this page, %s?".formatted(USER_NAME);

  @AfterAll
  static void tearDown() {
    closeWebDriver();
  }

  @BeforeAll
  static void beforeAll() {
    closeWebDriver();
  }

  @BeforeEach
  void openTestPage() {
    timeout = 1000;
    Configuration.browserCapabilities.setCapability(UNHANDLED_PROMPT_BEHAVIOUR, IGNORE);
    openFile("page_with_alerts.html");
    $("h1").shouldHave(text("Page with alerts"));
    $(By.name("username")).val(USER_NAME);
  }

  @Test
  void canSubmitConfirmDialogWithoutCheckingText() {
    $(byText("Confirm button")).click();
    String text = confirm();
    $("h1").shouldHave(text("Page with JQuery"));
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void canSubmitConfirmDialogAndCheckText() {
    $(byText("Confirm button")).click();
    confirm(ALERT_TEXT);
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  void canConfirm_withCustomTimeout() {
    timeout = 1;
    $(byText("Slow confirm")).click();
    confirm(withTimeout(ofSeconds(2)));
    $("h1").shouldHave(text("Page with JQuery"), ofSeconds(2));
  }

  @Test
  void canConfirm_withExpectedText_andCustomTimeout() {
    timeout = 1;
    $(byText("Slow confirm")).click();
    confirm(withExpectedText(ALERT_TEXT).timeout(ofSeconds(2)));
    $("h1").shouldHave(text("Page with JQuery"), ofSeconds(2));
  }

  @Test
  void canCancelConfirmDialog() {
    $(byText("Confirm button")).click();
    String text = dismiss();
    $("#message").shouldHave(text("Stay here, " + USER_NAME));
    $("#container").shouldNotBe(empty);
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void canCancelConfirmDialog_withExpectedText() {
    $(byText("Confirm button")).click();
    String text = dismiss(withExpectedText(ALERT_TEXT));
    $("#message").shouldHave(text("Stay here, " + USER_NAME));
    $("#container").shouldNotBe(empty);
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void canCancelConfirmDialog_withExpectedText_andCustomTimeout() {
    timeout = 1;
    $(byText("Confirm button")).click();
    String text = dismiss(withExpectedText(ALERT_TEXT).timeout(ofSeconds(2)));
    $("#message").shouldHave(text("Stay here, " + USER_NAME));
    $("#container").shouldNotBe(empty);
    assertThat(text).isEqualTo(ALERT_TEXT);
  }

  @Test
  void selenideChecksDialogText() {
    $(byText("Confirm button")).click();
    assertThatThrownBy(() -> confirm("Get out of this page, Maria?"))
      .isInstanceOf(DialogTextMismatch.class)
      .hasMessageContaining("Actual: Get out of this page, %s?".formatted(USER_NAME))
      .hasMessageContaining("Expected: Get out of this page, Maria?")
      .hasMessageMatching("(?s).*Page source: file:.+\\.html.*")
      .hasMessageMatching("(?s).*Timeout: .+m?s.*");
  }

  @Test
  void confirmReturnsActualDialogText() {
    $(byText("Confirm button")).click();
    assertThat(confirm()).isEqualTo(ALERT_TEXT);
  }

  @Test
  void dismissReturnsActualDialogText() {
    $(byText("Confirm button")).click();
    assertThat(dismiss()).isEqualTo(ALERT_TEXT);
  }

  @Test
  void dismissWithExpectedText() {
    $(byText("Confirm button")).click();
    assertThat(dismiss(ALERT_TEXT)).isEqualTo(ALERT_TEXT);
  }

  @Test
  void waitsUntilConfirmDialogAppears() {
    timeout = 2000;
    $(byText("Slow confirm")).click();
    String confirmDialogText = confirm();

    assertThat(confirmDialogText).isEqualTo(ALERT_TEXT);
  }

  @Test
  void canThrowExceptionInCaseOfUnexpectedConfirmDialog() {
    $(byText("Confirm button")).click();
    assertThatThrownBy(() -> $("h1").shouldHave(text("Page with JQuery")))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageStartingWith("UnhandledAlertException: ")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasCauseInstanceOf(UnhandledAlertException.class);
  }
}
