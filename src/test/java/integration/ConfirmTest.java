package integration;

import com.automation.remarks.video.annotations.Video;
import com.codeborne.selenide.ex.DialogTextMismatch;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.confirm;
import static com.codeborne.selenide.Selenide.dismiss;
import static com.codeborne.selenide.Selenide.onConfirmReturn;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static com.codeborne.selenide.WebDriverRunner.supportsModalDialogs;

class ConfirmTest extends IntegrationTest {
  private String userName = "John Mc'Clane";

  @AfterAll
  static void tearDown() {
    close();
  }

  @BeforeEach
  void openTestPage() {
    Assumptions.assumeFalse(isFirefox() || isChrome());
    openFile("page_with_alerts.html");
    $("h1").shouldHave(text("Page with alerts"));
    $(By.name("username")).val(userName);
  }

  @Test
  @Video
  void canSubmitConfirmDialogWithoutCheckingText() {
    onConfirmReturn(true);
    $(byText("Confirm button")).click();
    confirm();
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  @Video
  void canSubmitConfirmDialogAndCheckText() {
    onConfirmReturn(true);
    $(byText("Confirm button")).click();
    confirm("Get out of this page, " + userName + '?');
    $("h1").shouldHave(text("Page with JQuery"));
  }

  @Test
  @Video
  void canCancelConfirmDialog() {
    onConfirmReturn(false);
    $(byText("Confirm button")).click();
    dismiss("Get out of this page, " + userName + '?');
    $("#message").shouldHave(text("Stay here, " + userName));
    $("#container").shouldNotBe(empty);
  }

  @Test
  @Video
  void selenideChecksDialogText() {
    $(byText("Confirm button")).click();
    try {
      confirm("Get out of this page, Maria?");
    } catch (DialogTextMismatch expected) {
      return;
    }
    if (supportsModalDialogs()) {
      Assertions.fail("Should throw DialogTextMismatch for mismatching text");
    }
  }

  @Test
  @Video
  void confirmReturnsActualDialogText() {
    Assumptions.assumeTrue(supportsModalDialogs());

    $(byText("Confirm button")).click();
    Assertions.assertEquals("Get out of this page, " + userName + '?', confirm());
  }

  @Test
  @Video
  void dismissReturnsActualDialogText() {
    Assumptions.assumeTrue(supportsModalDialogs());

    $(byText("Confirm button")).click();
    Assertions.assertEquals("Get out of this page, " + userName + '?', dismiss());
  }

  @Test
  @Video
  void waitsUntilConfirmDialogAppears() {
    onConfirmReturn(true);
    $(byText("Slow confirm")).click();
    String confirmDialogText = confirm();

    if (!isHeadless()) {
      Assertions.assertEquals("Get out of this page, " + userName + '?', confirmDialogText);
    }
  }
}
