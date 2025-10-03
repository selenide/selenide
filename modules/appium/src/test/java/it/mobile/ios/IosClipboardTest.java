package it.mobile.ios;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.clipboard;
import static it.mobile.BrowserstackUtils.isCi;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

class IosClipboardTest extends BaseSwagLabsAppIosTest {
  @Test
  void canReadAndWriteClipboard() {
    clipboard().setText("Clipboard works in iOS");

    assumeThat(isCi())
      .as("in October 2025, clipboard was empty on BrowserStack..")
      .isFalse();

    assertThat(clipboard().getText()).isEqualTo("Clipboard works in iOS");
  }
}
