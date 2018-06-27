package com.codeborne.selenide.impl;

import com.codeborne.selenide.UnitTest;
import org.junit.jupiter.api.Test;

class NavigatorTest extends UnitTest {
  private Navigator navigator = new Navigator();

  @Test
  void detectsAbsoluteUrls() {
    assertThat(navigator.isAbsoluteUrl("http://selenide.org"))
      .as("protocol http")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("https://selenide.org"))
      .as("protocol https")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("file:///tmp/memory.dump"))
      .as("protocol file")
      .isTrue();

    assertThat(navigator.isAbsoluteUrl("HTTP://SELENIDE.ORG"))
      .as("case insensitive: HTTP")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("HTTPS://SELENIDE.ORG"))
      .as("case insensitive: HTTPS")
      .isTrue();
    assertThat(navigator.isAbsoluteUrl("FILE:///TMP/MEMORY.DUMP"))
      .as("case insensitive: FILE")
      .isTrue();

    assertThat(navigator.isAbsoluteUrl("/tmp/memory.dump"))
      .as("relative url")
      .isFalse();
    assertThat(navigator.isAbsoluteUrl("/payments/history"))
      .as("relative url")
      .isFalse();
  }

  @Test
  void addsRandomNumbersToEveryUrlToAvoidIECaching() {
    assertThat(navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com", 666))
      .isEqualTo("http://chuck-norris.com?timestamp=666");

    assertThat(navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com?timestamp=123456789", 666))
      .isEqualTo("http://chuck-norris.com?timestamp=666");

    assertThat(navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com?timestamp=123456789#", 666))
      .isEqualTo("http://chuck-norris.com?timestamp=666");

    assertThat(navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com?timestamp=123456789&abc=def", 666))
      .isEqualTo("http://chuck-norris.com?timestamp=666");
  }

  @Test
  void returnsAbsoluteUrl() {
    String baseUrl = "http://localhost:8080";
    String relativeUrl = "/users/id=1";
    String absoluteUrl = navigator.absoluteUrl(relativeUrl);
    assertThat(absoluteUrl)
      .isEqualTo(baseUrl + relativeUrl);
  }
}
