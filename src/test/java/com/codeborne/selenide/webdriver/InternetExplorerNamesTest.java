package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.UnitTest;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.WebDriverRunner.isIE;

class InternetExplorerNamesTest extends UnitTest {
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
