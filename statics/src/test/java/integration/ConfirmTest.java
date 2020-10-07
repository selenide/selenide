package integration;

import com.automation.remarks.video.annotations.Video;
import com.codeborne.selenide.ex.DialogTextMismatch;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.confirm;
import static com.codeborne.selenide.Selenide.dismiss;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ConfirmTest extends IntegrationTest {
  private static final String USER_NAME = "John Mc'Clane";

  @AfterAll
  static void tearDown() {
    closeWebDriver();
  }

  @BeforeEach
  void openTestPage() {
    timeout = 1000;
    openFile("page_with_alerts.html");
    $("h1").shouldHave(text("Page with alerts"));
    $(By.name("username")).val(USER_NAME);
  }

  @Test
  @Video
  void canSubmitConfirmDialogWithoutCheckingText() {
    $(byText("Confirm button")).click();
    confirm();
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  @Video
  void canSubmitConfirmDialogAndCheckText() {
    $(byText("Confirm button")).click();
    confirm("Get out of this page, " + USER_NAME + '?');
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  @Video
  void canCancelConfirmDialog() {
    $(byText("Confirm button")).click();
    dismiss("Get out of this page, " + USER_NAME + '?');
    $("#message").shouldHave(text("Stay here, " + USER_NAME));
    $("#container").shouldNotBe(empty);
  }

  @Test
  @Video
  void selenideChecksDialogText() {
    $(byText("Confirm button")).click();
    assertThatThrownBy(() -> confirm("Get out of this page, Maria?"))
      .isInstanceOf(DialogTextMismatch.class)
      .hasMessageContaining("Actual: Get out of this page, John Mc'Clane?")
      .hasMessageContaining("Expected: Get out of this page, Maria?")
      .hasMessageMatching("(?s).*Screenshot: file:.+\\.png.*")
      .hasMessageMatching("(?s).*Page source: file:.+\\.html.*")
      .hasMessageMatching("(?s).*Timeout: .+ m?s\\..*");
  }

  @Test
  @Video
  void confirmReturnsActualDialogText() {
    $(byText("Confirm button")).click();
    assertThat(confirm())
      .isEqualTo(String.format("Get out of this page, %s?", USER_NAME));
  }

  @Test
  @Video
  void dismissReturnsActualDialogText() {
    $(byText("Confirm button")).click();
    assertThat(dismiss())
      .isEqualTo(String.format("Get out of this page, %s?", USER_NAME));
  }

  @Test
  @Video
  void waitsUntilConfirmDialogAppears() {
    timeout = 2000;
    $(byText("Slow confirm")).click();
    String confirmDialogText = confirm();

    assertThat(confirmDialogText)
      .isEqualTo(String.format("Get out of this page, %s?", USER_NAME));
  }
}
