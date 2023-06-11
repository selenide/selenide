package it.selenoid;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.ClipboardConditions.content;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.clipboard;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SelenoidSetup.class)
public class SelenoidClipboardTest {

  @BeforeEach
  public void prepare() {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = HTTPGET;

    open("/clipboard.html");
  }

  @Test
  public void getClipboardContent() {
    $("#text-input").shouldHave(attribute("value", "Hello World"));
    $("#copy-button").shouldBe(visible).click();
    clipboard().shouldHave(content("Hello World"));
    assertThat(clipboard().getText()).isEqualTo("Hello World");
  }

  @Test
  public void setClipboardContent() {
    clipboard().setText("John Wick");
    clipboard().shouldHave(content("John Wick"));
    assertThat(clipboard().getText()).isEqualTo("John Wick");
  }

  @Test
  public void setAndGetClipboardMultilineContent() {
    String multilineText = "John\nWick\r\nThe\nGreat\r";
    clipboard().setText(multilineText);
    clipboard().shouldHave(content(multilineText));
    assertThat(clipboard().getText()).isEqualTo(multilineText);
  }
}
