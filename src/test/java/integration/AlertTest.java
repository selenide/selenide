package integration;

import com.codeborne.selenide.ex.DialogTextMismatch;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byValue;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.confirm;
import static com.codeborne.selenide.Selenide.prompt;
import static com.codeborne.selenide.WebDriverRunner.supportsModalDialogs;

class AlertTest extends IntegrationTest {
  @AfterAll
  static void tearDown() {
    close();
  }

  @BeforeEach
  void openTestPage() {
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
    prompt("Please input your username", "Aegon Targaryen");
    $("#message").shouldHave(text("Hello, Aegon Targaryen!"));
    $("#container").shouldBe(empty);
  }

  @Test
  void selenideChecksDialogText() {
    $(By.name("username")).val("Gregg");
    $(byValue("Alert button")).click();
    try {
      confirm("Good bye, Greg!");
    } catch (DialogTextMismatch expected) {
      return;
    }
    if (supportsModalDialogs()) {
      Assertions.fail("Should throw DialogTextMismatch for mismatching text");
    }
  }

  @Test
  void waitsUntilAlertDialogAppears() {
    $(By.name("username")).val("Быстрый Гарри");
    $(byValue("Slow alert")).click();
    confirm("Are you sure, Быстрый Гарри?");
    $("#message").shouldHave(text("Hello, Быстрый Гарри!"));
    $("#container").shouldBe(empty);
  }

  @Test
  void waitsUntilPromptDialogAppears() {
    $(By.name("username")).val("Медленный Барри");
    $(byValue("Slow prompt")).click();
    prompt("Медленный Барри");
    $("#message").shouldHave(text("Hello, Медленный Барри!"));
    $("#container").shouldBe(empty);
  }
}
