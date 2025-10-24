package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;

final class ScreenshotOfFocusableElementTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    timeout = 4000;
    openFile("page_with_focusable_element.html");
  }

  @Test
  void canCheckWhichElementIsFocusedNow() {
    $("#username").shouldNotBe(focused);
    $("#username").sendKeys("John McClane");
    $("#username").shouldBe(focused);
    $("#username-mirror").shouldBe(visible, partialText("Focused: "));
  }
}
