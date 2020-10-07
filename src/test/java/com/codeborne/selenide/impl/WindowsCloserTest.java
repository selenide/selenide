package com.codeborne.selenide.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class WindowsCloserTest {
  private final WebDriver webdriver = mock(WebDriver.class);
  private final WindowsCloser windowsCloser = new WindowsCloser();

  @BeforeEach
  void setUp() {
    when(webdriver.switchTo()).thenReturn(mock(WebDriver.TargetLocator.class));
  }

  @Test
  void closesNewWindowIfFileWasOpenedInSeparateWindow() throws IOException {
    when(webdriver.getWindowHandle()).thenReturn("tab1");
    when(webdriver.getWindowHandles())
      .thenReturn(new HashSet<>(asList("tab1", "tab2", "tab3")))
      .thenReturn(new HashSet<>(asList("tab1", "tab2", "tab3", "tab-with-pdf")));

    String status = windowsCloser.runAndCloseArisedWindows(webdriver, () -> "Done");

    assertThat(status).isEqualTo("Done");
    verify(webdriver.switchTo()).window("tab-with-pdf");
    verify(webdriver).close();
    verify(webdriver.switchTo()).window("tab1");
    verifyNoMoreInteractions(webdriver.switchTo());
  }

  @Test
  void ignoresErrorIfWindowHasAlreadyBeenClosedMeanwhile() throws IOException {
    WebDriver.TargetLocator targetLocator = mock(WebDriver.TargetLocator.class);
    doReturn(targetLocator).when(webdriver).switchTo();
    doThrow(new NoSuchWindowException("no window: tab-with-pdf")).when(targetLocator).window("tab-with-pdf");

    when(webdriver.getWindowHandle()).thenReturn("tab1");
    when(webdriver.getWindowHandles())
      .thenReturn(new HashSet<>(asList("tab1", "tab2", "tab3")))
      .thenReturn(new HashSet<>(asList("tab1", "tab2", "tab3", "tab-with-pdf")));

    String status = windowsCloser.runAndCloseArisedWindows(webdriver, () -> "Done");

    assertThat(status).isEqualTo("Done");
    verify(webdriver.switchTo()).window("tab-with-pdf");
    verify(webdriver, never()).close();
    verify(webdriver.switchTo()).window("tab1");
    verifyNoMoreInteractions(webdriver.switchTo());
  }
}
