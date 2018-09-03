package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.WebDriverRunner.isIE;

class InternetExplorerNamesTest implements WithAssertions {
  @Test
  void internetExplorerShortNameTest() {
    Configuration.browser = WebDriverRunner.IE;
    assertThat(isIE()).isTrue();
  }

  @Test
  void internetExplorerFullNameTest() {
    Configuration.browser = WebDriverRunner.INTERNET_EXPLORER;
    assertThat(isIE()).isTrue();
  }
}
