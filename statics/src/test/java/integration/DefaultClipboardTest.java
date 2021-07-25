package integration;

import com.codeborne.selenide.ex.ConditionMetException;
import com.codeborne.selenide.ex.ConditionNotMetException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClipboardConditions.content;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.clipboard;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Need configure X11 for github actions, not sure that it needs")
public class DefaultClipboardTest extends IntegrationTest {

  @BeforeEach
  public void openTestPage() {
    openFile("clipboard.html");
  }

  @Test
  public void getClipboard() {
    $("#text-to-copy").shouldHave(attribute("value", "Hello World"));
    $("#button-copy-text").shouldBe(visible).click();
    assertEquals("Hello World", clipboard().getText(), "Clipboard data doesn't match");
  }

  @Test
  public void waitForClipboardContent() {
    $("#text-to-copy").val("Hello slow World");
    $("#button-copy-text-slowly").click();
    clipboard().shouldHave(content("Hello slow World"), ofMillis(1500));
  }

  @Test
  public void errorMessage() {
    $("#button-copy-text-slowly").click();
    assertThatThrownBy(() ->
      clipboard().shouldHave(content("Goodbye World"), ofMillis(1500))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("clipboard should have content 'Goodbye World'")
      .hasMessageContaining("Actual value: Hello World")
      .hasMessageContaining("Timeout: 1.500 s.");
  }

  @Test
  public void errorMessage_negativeCase() {
    $("#button-copy-text").click();
    assertThatThrownBy(() ->
      clipboard().shouldNotHave(content("Hello World"), ofMillis(300))
    )
      .isInstanceOf(ConditionMetException.class)
      .hasMessageStartingWith("clipboard should not have content 'Hello World'")
      .hasMessageContaining("Actual value: Hello World")
      .hasMessageContaining("Timeout: 300 ms.");
  }

  @Test
  public void checkSetValue() {
    clipboard().setText("111");
    assertEquals("111", clipboard().getText(), "Clipboard data doesn't match");
  }

  @AfterAll
  public static void tearDown() {
    closeWebDriver();
  }

}
