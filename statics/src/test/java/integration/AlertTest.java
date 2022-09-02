package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byValue;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.confirm;
import static org.openqa.selenium.UnexpectedAlertBehaviour.ACCEPT_AND_NOTIFY;
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
    Configuration.browserCapabilities.setCapability(UNHANDLED_PROMPT_BEHAVIOUR, ACCEPT_AND_NOTIFY);
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
}
