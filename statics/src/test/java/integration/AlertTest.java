package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.ex.AlertNotFoundError;
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
import static com.codeborne.selenide.Selectors.byValue;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.confirm;
import static com.codeborne.selenide.Selenide.prompt;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.openqa.selenium.UnexpectedAlertBehaviour.IGNORE;
import static org.openqa.selenium.remote.CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR;

final class AlertTest extends IntegrationTest {
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
    Configuration.browserCapabilities.setCapability(UNHANDLED_PROMPT_BEHAVIOUR, IGNORE);
    openFile("page_with_alerts.html");
  }

  @Test
  void canSubmitAlertDialog() {
    $(By.name("username")).val("Greg");
    $(byValue("Alert button")).click();
    confirm("Are you sure, Greg?");
    $("#message").shouldHave(text("Hello, Greg!"));
    $("#container").shouldBe(empty);
  }

  @Test
  void canSubmitPromptDialogWithDefaultValue() {
    $(byValue("Prompt button")).click();
    prompt();
    $("#message").shouldHave(text("Hello, default!"));
    $("#container").shouldBe(empty);
  }

  @Test
  void canSubmitPromptDialog() {
    $(byValue("Prompt button")).click();
    String text = prompt("Please input your username", "Aegon Targaryen");
    $("#message").shouldHave(text("Hello, Aegon Targaryen!"));
    $("#container").shouldBe(empty);
    assertThat(text).isEqualTo("Please input your username");
  }

  @Test
  void canThrowExceptionInCaseOfUnexpectedAlert() {
    Configuration.timeout = 20;
    $(By.name("username")).val("Greg");
    $(byValue("Alert button")).click();

    assertThatThrownBy(() -> $("#message").shouldHave(text("Hello, Greg!")))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageStartingWith("UnhandledAlertException: ")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasCauseInstanceOf(UnhandledAlertException.class);
  }

  @Test
  void selenideChecksDialogText() {
    $(By.name("username")).val("Gregg");
    $(byValue("Alert button")).click();

    assertThatThrownBy(() -> confirm("Good bye, Gregg!"))
      .isInstanceOf(DialogTextMismatch.class)
      .hasMessageStartingWith("Dialog text mismatch")
      .hasMessageContaining("Actual: Are you sure, Gregg?")
      .hasMessageContaining("Expected: Good bye, Gregg!");
  }

  @Test
  void waitsUntilAlertDialogAppears() {
    Configuration.timeout = 4000;
    $(By.name("username")).val("Быстрый Гарри");
    $(byValue("Slow alert")).click();
    confirm("Are you sure, Быстрый Гарри?");
    $("#message").shouldHave(text("Hello, Быстрый Гарри!"));
    $("#container").shouldBe(empty);
  }

  @Test
  void waitsUntilPromptDialogAppears() {
    Configuration.timeout = 4000;
    $(By.name("username")).val("Медленный Барри");
    $(byValue("Slow prompt")).click();
    prompt("Медленный Барри");
    $("#message").shouldHave(text("Hello, Медленный Барри!"));
    $("#container").shouldBe(empty);
  }

  @Test
  void alertThrowsNoAlertPresentExceptionWhenAlertIsNotPresent() {
    assertThatThrownBy(() -> switchTo().alert())
      .isInstanceOf(AlertNotFoundError.class)
      .hasMessageStartingWith("Alert not found")
      .hasMessageContaining("Screenshot: file:")
      .hasMessageContaining("Page source: file:")
      .hasMessageContaining("Caused by: TimeoutException:");
  }

  @Test
  void confirmThrowsNoAlertPresentExceptionWhenAlertIsNotPresent() {
    assertThatThrownBy(Selenide::confirm)
      .isInstanceOf(AlertNotFoundError.class)
      .hasMessageStartingWith("Alert not found")
      .hasMessageContaining("Screenshot: file:")
      .hasMessageContaining("Page source: file:")
      .hasMessageContaining("Caused by: TimeoutException:");
  }

  @Test
  void promptThrowsNoAlertPresentExceptionWhenAlertIsNotPresent() {
    assertThatThrownBy(Selenide::prompt)
      .isInstanceOf(AlertNotFoundError.class)
      .hasMessageStartingWith("Alert not found")
      .hasMessageContaining("Screenshot: file:")
      .hasMessageContaining("Page source: file:")
      .hasMessageContaining("Caused by: TimeoutException:");
  }
}
