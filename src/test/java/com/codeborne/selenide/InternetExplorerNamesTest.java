package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class InternetExplorerNamesTest {
  @Test
  void internetExplorerShortNameTest() {
    assertThat(new Browser(Browsers.IE, false).isIE()).isTrue();
  }

  @Test
  void internetExplorerFullNameTest() {
    assertThat(new Browser(Browsers.INTERNET_EXPLORER, false).isIE()).isTrue();
  }
}
