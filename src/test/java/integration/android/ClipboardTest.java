package integration.android;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClipboardConditions.content;
import static com.codeborne.selenide.Selenide.clipboard;
import static org.assertj.core.api.Assertions.assertThat;

public class ClipboardTest extends BaseAndroidTest {
  @Test
  public void canAccessClipboardInMobile() {
    clipboard().setText("This is Appium, Виталик&");

    clipboard().shouldHave(content("This is Appium, Виталик&"));
    assertThat(clipboard().getText()).isEqualTo("This is Appium, Виталик&");
  }
}
