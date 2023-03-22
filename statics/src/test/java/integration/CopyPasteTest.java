package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.clipboard;
import static com.codeborne.selenide.Selenide.copy;
import static com.codeborne.selenide.Selenide.getSelectedText;
import static org.assertj.core.api.Assertions.assertThat;

final class CopyPasteTest extends IntegrationTest {

  @BeforeEach
  public void clipboardCheck() {
    assumeClipboardSupported();
  }

  @Test
  void copyEmptySelection() {
    openFile("start_page.html");
    clipboard().setText("text");
    copy();
    assertThat(clipboard().getText()).isEmpty();
  }

  @Test
  void copyNonEmptySelection() {
    openFile("start_page.html");
    $("#greeting").doubleClick();
    assertThat(getSelectedText()).isEqualTo("Hello_my_friend");
    copy();
    assertThat(clipboard().getText()).isEqualTo("Hello_my_friend");
  }

  @Test
  void pasteToField() {
    openFile("page_with_inputs_and_hints.html");
    clipboard().setText("text");
    $("#username")
      .paste()
      .shouldHave(value("text"));
  }
}
