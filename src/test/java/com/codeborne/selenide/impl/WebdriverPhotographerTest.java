package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

final class WebdriverPhotographerTest {
  private final WebdriverPhotographer photographer = new WebdriverPhotographer();

  @Test
  void devToolsThrows_fallsBackToTakesScreenshot() {
    DevToolsDriver driver = mock(DevToolsDriver.class);
    DevTools devTools = mock(DevTools.class);
    doReturn(devTools).when(driver).getDevTools();
    doThrow(new RuntimeException("no-op implementation")).when(devTools).send(any(), any());
    doReturn("window1").when(driver).getWindowHandle();

    byte[] expectedScreenshot = {1, 2, 3};
    doReturn(expectedScreenshot).when(driver).getScreenshotAs(OutputType.BYTES);

    Optional<byte[]> result = photographer.takeScreenshot(driver, OutputType.BYTES);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(expectedScreenshot);
    verify(driver).getScreenshotAs(OutputType.BYTES);
  }

  @Test
  void transientError_doesNotDisableDevToolsOnNextCall() {
    DevToolsDriver driver = mock(DevToolsDriver.class);
    DevTools devTools = mock(DevTools.class);
    doReturn(devTools).when(driver).getDevTools();
    doReturn("window1").when(driver).getWindowHandle();

    // First call: DevTools throws, falls back to TakesScreenshot
    doThrow(new RuntimeException("navigation in progress")).when(devTools).send(any(), any());
    byte[] fallbackScreenshot = {1, 2, 3};
    doReturn(fallbackScreenshot).when(driver).getScreenshotAs(OutputType.BYTES);
    assertThat(photographer.takeScreenshot(driver, OutputType.BYTES)).contains(fallbackScreenshot);

    // Second call: DevTools succeeds — verify it's attempted again
    verify(devTools, times(1)).send(any(), any());

    // DevTools is called again on next invocation (not permanently disabled)
    doThrow(new RuntimeException("another transient error")).when(devTools).send(any(), any());
    assertThat(photographer.takeScreenshot(driver, OutputType.BYTES)).contains(fallbackScreenshot);
    verify(devTools, times(2)).send(any(), any());
  }

  @Test
  void noSupportedInterfaces_returnsEmpty() {
    WebDriver driver = mock(WebDriver.class);

    Optional<byte[]> result = photographer.takeScreenshot(driver, OutputType.BYTES);

    assertThat(result).isEmpty();
  }

  private interface DevToolsDriver extends WebDriver, HasDevTools, TakesScreenshot {
  }
}
