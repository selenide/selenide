package com.codeborne.selenide.impl;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;

class NavigatorTest {
  private Navigator navigator = new Navigator();

  @Test
  void detectsAbsoluteUrls() {
    MatcherAssert.assertThat("protocol http", navigator.isAbsoluteUrl("http://selenide.org"), is(true));
    MatcherAssert.assertThat("protocol https", navigator.isAbsoluteUrl("https://selenide.org"), is(true));
    MatcherAssert.assertThat("protocol file", navigator.isAbsoluteUrl("file:///tmp/memory.dump"), is(true));

    MatcherAssert.assertThat("case insensitive: HTTP", navigator.isAbsoluteUrl("HTTP://SELENIDE.ORG"), is(true));
    MatcherAssert.assertThat("case insensitive: HTTPS", navigator.isAbsoluteUrl("HTTPS://SELENIDE.ORG"), is(true));
    MatcherAssert.assertThat("case insensitive: FILE", navigator.isAbsoluteUrl("FILE:///TMP/MEMORY.DUMP"), is(true));

    MatcherAssert.assertThat("relative url", navigator.isAbsoluteUrl("/tmp/memory.dump"), is(false));
    MatcherAssert.assertThat("relative url", navigator.isAbsoluteUrl("/payments/history"), is(false));
  }

  @Test
  void addsRandomNumbersToEveryUrlToAvoidIECaching() {
    Assertions.assertEquals("http://chuck-norris.com?timestamp=666",
      navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com", 666));

    Assertions.assertEquals("http://chuck-norris.com?timestamp=666",
      navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com?timestamp=123456789", 666));

    Assertions.assertEquals("http://chuck-norris.com?timestamp=666",
      navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com?timestamp=123456789#", 666));

    Assertions.assertEquals("http://chuck-norris.com?timestamp=666",
      navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com?timestamp=123456789&abc=def", 666));
  }

  @Test
  void returnsAbsoluteUrl() {
    String baseUrl = "http://localhost:8080";
    String relativeUrl = "/users/id=1";
    String absoluteUrl = navigator.absoluteUrl(relativeUrl);
    Assertions.assertEquals(absoluteUrl, baseUrl + relativeUrl);
  }
}
