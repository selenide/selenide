package com.codeborne.selenide.webdriver;

import java.util.Map;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.UnitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.opera.OperaOptions;

import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
class OperaDriverFactoryTest extends UnitTest {
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
    assertThat(options.get("binary"))
      .isEqualTo("c:/browser.exe");
  }

  @Test
  void headlessCanNotBeSet() {
    Configuration.headless = true;
    assertThatThrownBy(() -> new OperaDriverFactory().createOperaOptions(proxy))
      .isInstanceOf(InvalidArgumentException.class);
  }
}
