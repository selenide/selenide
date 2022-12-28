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
import static org.openqa.selenium.UnexpectedAlertBehaviour.ACCEPT_AND_NOTIFY;
import static org.openqa.selenium.remote.CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR;

final class ConfirmTest extends IntegrationTest {
  private static final String USER_NAME = "John Mc'Clane";

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
    Configuration.browserCapabilities.setCapability(UNHANDLED_PROMPT_BEHAVIOUR, ACCEPT_AND_NOTIFY);
    openFile("page_with_alerts.html");
    $("h1").shouldHave(text("Page with alerts"));
    $(By.name("username")).val(USER_NAME);
  }

  @Test
  void canSubmitConfirmDialogWithoutCheckingText() {
    $(byText("Confirm button")).click();
    confirm();
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  void canSubmitConfirmDialogAndCheckText() {
    $(byText("Confirm button")).click();
    confirm("Get out of this page, " + USER_NAME + '?');
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
    confirm(withExpectedText("Get out of this page, " + USER_NAME + '?').timeout(ofSeconds(2)));
    $("h1").shouldHave(text("Page with JQuery"), ofSeconds(2));
  }

  @Test
  void canCancelConfirmDialog() {
    $(byText("Confirm button")).click();
    dismiss();
    $("#message").shouldHave(text("Stay here, " + USER_NAME));
    $("#container").shouldNotBe(empty);
  }

  @Test
  void canCancelConfirmDialog_withExpectedText() {
    $(byText("Confirm button")).click();
    dismiss(withExpectedText("Get out of this page, " + USER_NAME + '?'));
    $("#message").shouldHave(text("Stay here, " + USER_NAME));
    $("#container").shouldNotBe(empty);
  }

  @Test
  void canCancelConfirmDialog_withExpectedText_andCustomTimeout() {
    timeout = 1;
    $(byText("Confirm button")).click();
    dismiss(withExpectedText("Get out of this page, " + USER_NAME + '?').timeout(ofSeconds(2)));
    $("#message").shouldHave(text("Stay here, " + USER_NAME));
    $("#container").shouldNotBe(empty);
  }

  @Test
  void selenideChecksDialogText() {
    $(byText("Confirm button")).click();
    assertThatThrownBy(() -> confirm("Get out of this page, Maria?"))
      .isInstanceOf(DialogTextMismatch.class)
      .hasMessageContaining("Actual: Get out of this page, John Mc'Clane?")
      .hasMessageContaining("Expected: Get out of this page, Maria?")
      .hasMessageMatching("(?s).*Page source: file:.+\\.html.*")
      .hasMessageMatching("(?s).*Timeout: .+ m?s\\..*");
  }

  @Test
  void confirmReturnsActualDialogText() {
    $(byText("Confirm button")).click();
    assertThat(confirm())
      .isEqualTo(String.format("Get out of this page, %s?", USER_NAME));
  }

  @Test
  void dismissReturnsActualDialogText() {
    $(byText("Confirm button")).click();
    assertThat(dismiss())
      .isEqualTo(String.format("Get out of this page, %s?", USER_NAME));
  }

  @Test
  void waitsUntilConfirmDialogAppears() {
    timeout = 2000;
    $(byText("Slow confirm")).click();
    String confirmDialogText = confirm();

    assertThat(confirmDialogText)
      .isEqualTo(String.format("Get out of this page, %s?", USER_NAME));
  }

  @Test
  void canThrowExceptionInCaseOfUnexpectedConfirmDialog() {
    $(byText("Confirm button")).click();
    assertThatThrownBy(() -> $("h1").shouldHave(text("Page with JQuery")))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageStartingWith("UnhandledAlertException: ")
      .hasMessageContaining("Get out of this page, John Mc'Clane?")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasCauseInstanceOf(UnhandledAlertException.class);
  }

}
