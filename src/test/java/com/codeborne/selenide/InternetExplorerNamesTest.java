package com.codeborne.selenide;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

final class InternetExplorerNamesTest implements WithAssertions {
  @Test
  void internetExplorerShortNameTest() {
    assertThat(new Browser(Browsers.IE, false).isIE()).isTrue();
  }

  @Test
  void internetExplorerFullNameTest() {
    assertThat(new Browser(Browsers.INTERNET_EXPLORER, false).isIE()).isTrue();
  }
}
