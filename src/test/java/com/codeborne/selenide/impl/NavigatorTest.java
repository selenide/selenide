package com.codeborne.selenide.impl;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class NavigatorTest {
  private Navigator navigator = new Navigator();

  @Test
  public void detectsAbsoluteUrls() {
    assertThat("protocol http", navigator.isAbsoluteUrl("http://selenide.org"), is(true));
    assertThat("protocol https", navigator.isAbsoluteUrl("https://selenide.org"), is(true));
    assertThat("protocol file", navigator.isAbsoluteUrl("file:///tmp/memory.dump"), is(true));

    assertThat("case insensitive: HTTP", navigator.isAbsoluteUrl("HTTP://SELENIDE.ORG"), is(true));
    assertThat("case insensitive: HTTPS", navigator.isAbsoluteUrl("HTTPS://SELENIDE.ORG"), is(true));
    assertThat("case insensitive: FILE", navigator.isAbsoluteUrl("FILE:///TMP/MEMORY.DUMP"), is(true));

    assertThat("relative url", navigator.isAbsoluteUrl("/tmp/memory.dump"), is(false));
    assertThat("relative url", navigator.isAbsoluteUrl("/payments/history"), is(false));
  }

  @Test
  public void addsRandomNumbersToEveryUrlToAvoidIECaching() {
    assertEquals("http://chuck-norris.com?timestamp=666",
        navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com", 666));

    assertEquals("http://chuck-norris.com?timestamp=666",
        navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com?timestamp=123456789", 666));

    assertEquals("http://chuck-norris.com?timestamp=666",
        navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com?timestamp=123456789#", 666));

    assertEquals("http://chuck-norris.com?timestamp=666",
        navigator.makeUniqueUrlToAvoidIECaching("http://chuck-norris.com?timestamp=123456789&abc=def", 666));
  }

  @Test
  public void returnsAbsoluteUrl() {
    String baseUrl = "http://localhost:8080";
    String relativeUrl = "/users/id=1";
    String absoluteUrl = navigator.absoluteUrl(relativeUrl);
    assertEquals(absoluteUrl, baseUrl + relativeUrl);
  }
}
