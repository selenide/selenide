package com.codeborne.selenide.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.remote.Browser.CHROME;
import static org.openqa.selenium.remote.Browser.SAFARI;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.DevToolsException;
import org.openqa.selenium.remote.Browser;

class CdpProviderTest {
  private final MyCdp cdp = new MyCdp();

  @Test
  void shouldAllowChromium() {
    MyDriver driverMock = mockFor(CHROME);
    cdp.requireChromium(driverMock);
  }

  @Test
  void shouldThrowErrorForNotChromium() {
    MyDriver driverMock = mockFor(SAFARI);
    assertThatThrownBy(() -> cdp.requireChromium(driverMock))
      .isInstanceOf(DevToolsException.class)
      .hasMessageContaining("Cannot create devtools for non-chromium browser");
  }

  @Test
  void shouldThrowErrorWhenNoCapabilities() {
    WebDriver driverMock = mock(WebDriver.class);
    assertThatThrownBy(() -> cdp.requireChromium(driverMock))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Driver must have capabilities");
  }

  private MyDriver mockFor(Browser safari) {
    MyDriver driverMock = mock(MyDriver.class);
    var caps = new MutableCapabilities();
    caps.setCapability("browserName", safari.browserName());
    when(driverMock.getCapabilities()).thenReturn(caps);
    return driverMock;
  }

  static class MyCdp implements CdpProvider {
    @Override
    public DevTools getCdp(Driver driver) {
      return null;
    }
  }

  interface MyDriver extends HasCapabilities, WebDriver {

  }
}
