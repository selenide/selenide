package it.mobile.android;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.clipboard;
import static org.assertj.core.api.Assertions.assertThat;

class AndroidClipboardTest extends BaseApiDemosTest {
  @Test
  void canReadAndWriteClipboard() {
    clipboard().setText("Clipboard works in Android");
    assertThat(clipboard().getText()).isEqualTo("Clipboard works in Android");
  }
}
