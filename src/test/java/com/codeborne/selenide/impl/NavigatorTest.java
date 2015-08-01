package com.codeborne.selenide.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NavigatorTest {
  Navigator navigator = new Navigator();

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
}
