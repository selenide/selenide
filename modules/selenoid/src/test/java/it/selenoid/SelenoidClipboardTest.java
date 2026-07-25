package it.selenoid;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
  private static final Logger log = LoggerFactory.getLogger(SelenoidClipboardTest.class);

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
    assertClipboardContains("Hello World");
  }

  @Test
  public void setClipboardContent() {
    clipboard().setText("John Wick");
    assertClipboardContains("John Wick");
  }

  @Test
  public void setAndGetClipboardMultilineContent() {
    String multilineText = "John\nWick\r\nThe\nGreat\r";
    clipboard().setText(multilineText);
    assertClipboardContains(multilineText);
  }

  private void assertClipboardContains(String expectedText) {
    // Selenide style
    clipboard().shouldHave(content(expectedText).or(content("")));

    // AssertJ style
    assertThat(List.of(expectedText, "")).contains(clipboard().getText());
  }
}
