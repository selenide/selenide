package com.codeborne.selenide.impl;

import com.codeborne.selenide.drivercommands.BrowserHealthChecker;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

final class BrowserHealthCheckerTest {
  private final WebDriver webdriver = mock();
  private final BrowserHealthChecker checker = new BrowserHealthChecker();

  @Test
  void checksIfBrowserIsStillAlive_byCallingGetTitle() {
    doReturn("blah").when(webdriver).getTitle();

    assertThat(checker.isBrowserStillOpen(webdriver)).isTrue();
  }

  @Test
  void isBrowserStillOpen_UnreachableBrowserException() {
    doThrow(new UnreachableBrowserException("oops")).when(webdriver).getTitle();

    assertThat(checker.isBrowserStillOpen(webdriver)).isFalse();
  }

  @Test
  void isBrowserStillOpen_NoSuchWindowException() {
    doThrow(new NoSuchWindowException("oops")).when(webdriver).getTitle();

    assertThat(checker.isBrowserStillOpen(webdriver)).isFalse();
  }

  @Test
  void isBrowserStillOpen_NoSuchSessionException() {
    doThrow(new NoSuchSessionException("oops")).when(webdriver).getTitle();

    assertThat(checker.isBrowserStillOpen(webdriver)).isFalse();
  }

  @Test
  void isBrowserStillOpen_UnsupportedCommandException_means_webdriverIsStillAlive() {
    doThrow(new UnsupportedCommandException("this is Appium")).when(webdriver).getTitle();

    assertThat(checker.isBrowserStillOpen(webdriver)).isTrue();
  }
}
