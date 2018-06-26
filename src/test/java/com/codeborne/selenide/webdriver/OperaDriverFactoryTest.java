package com.codeborne.selenide.webdriver;

import java.util.Map;

import com.codeborne.selenide.Configuration;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.opera.OperaOptions;

import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
class OperaDriverFactoryTest {

  private Proxy proxy = mock(Proxy.class);

  @AfterEach
  void tearDown() {
    Configuration.browserBinary = "";
    Configuration.headless = false;
  }

  @Test
  void browserBinaryCanBeSet() {
    Configuration.browserBinary = "c:/browser.exe";
    Capabilities caps = new OperaDriverFactory().createOperaOptions(proxy);
    Map options = (Map) caps.asMap().get(OperaOptions.CAPABILITY);
    MatcherAssert.assertThat(options.get("binary"), Matchers.is("c:/browser.exe"));
  }

  @Test
  void headlessCanNotBeSet() {
    Configuration.headless = true;
    Assertions.assertThrows(InvalidArgumentException.class, () -> {
      Capabilities caps = new OperaDriverFactory().createOperaOptions(proxy);
    });
  }
}
