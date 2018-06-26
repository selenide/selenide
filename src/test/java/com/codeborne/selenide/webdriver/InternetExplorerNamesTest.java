package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class InternetExplorerNamesTest {
  @Test
  void internetExplorerShortNameTest() {
    Configuration.browser = "ie";
    assertThat(isIE(), is(true));
  }

  @Test
  void internetExplorerFullNameTest() {
    Configuration.browser = "internet explorer";
    assertThat(isIE(), is(true));
  }
}
