package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.*;
import org.hamcrest.Matchers;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.opera.OperaOptions;

import java.util.*;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
public class OperaDriverFactoryTest {

  private Proxy proxy = mock(Proxy.class);

  @After
  public void tearDown() {
    Configuration.browserBinary = "";
    Configuration.headless = false;
  }

  @Test
  public void browserBinaryCanBeSet() {
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = new OperaDriverFactory().createOperaOptions(proxy);
    Map options = (Map) caps.asMap().get(OperaOptions.CAPABILITY);
    assertThat(options.get("binary"), Matchers.is("c:/browser.exe"));
  }

  @Test(expected = InvalidArgumentException.class)
  public void headlessCanNotBeSet() {
    Configuration.headless = true;
    Capabilities caps = new OperaDriverFactory().createOperaOptions(proxy);

  }
}
