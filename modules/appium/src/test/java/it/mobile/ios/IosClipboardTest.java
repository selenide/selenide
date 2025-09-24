package it.mobile.ios;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.clipboard;
import static org.assertj.core.api.Assertions.assertThat;

class IosClipboardTest extends BaseSwagLabsAppIosTest {
  @Test
  void canReadAndWriteClipboard() {
    clipboard().setText("Clipboard works in iOS");
    assertThat(clipboard().getText()).isEqualTo("Clipboard works in iOS");
  }
}
