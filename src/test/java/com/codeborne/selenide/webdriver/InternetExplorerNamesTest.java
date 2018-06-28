package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.WebDriverRunner.isIE;

class InternetExplorerNamesTest implements WithAssertions {
  @Test
  void internetExplorerShortNameTest() {
    Configuration.browser = "ie";
    assertThat(isIE())
      .isTrue();
  }

  @Test
  void internetExplorerFullNameTest() {
    Configuration.browser = "internet explorer";
    assertThat(isIE())
      .isTrue();
  }
}
