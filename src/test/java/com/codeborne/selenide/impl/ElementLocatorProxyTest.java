package com.codeborne.selenide.impl;

import org.junit.Test;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElementLocatorProxyTest {
  @Test
  public void testToString() {
    ElementLocator locator = mock(ElementLocator.class);
    when(locator.toString()).thenReturn("pageObjectField");

    assertEquals("{pageObjectField}", new ElementLocatorProxy(locator).toString());
  }
}
