package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;
import static org.assertj.core.api.Assertions.assertThat;

final class CopyPasteTest extends IntegrationTest {

  @BeforeEach
  public void clipboardCheck() {
    assumeClipboardSupported();
  }

  @Test
  void copyEmptySelection() {
    openFile("start_page.html");
    Selenide.clipboard().setText("text");
    Selenide.copy();
    assertThat(Selenide.clipboard().getText()).isEmpty();
  }

  @Test
  void copyNonEmptySelection() {
    openFile("start_page.html");
    Selenide.$("h1").doubleClick();
    Selenide.copy();
    assertThat(Selenide.clipboard().getText()).isEqualTo("Selenide");
  }

  @Test
  void pasteToField() {
    openFile("page_with_inputs_and_hints.html");
    Selenide.clipboard().setText("text");
    Selenide.$("#username").paste().shouldHave(exactText("text"));
  }
}
