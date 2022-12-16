package com.codeborne.selenide.impl.windows;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FrameByIdOrNameTest {
  @Test
  void searchesFrameOrIframeByIdOrName() {
    assertThat(new FrameByIdOrName("menu"))
      .hasToString("frame to be available: By.cssSelector: frame#menu,frame[name=menu],iframe#menu,iframe[name=menu]");
  }

  @Test
  void apply_shouldIgnore_noSuchFrameException() {
    WebDriver webdriver = mock();
    doThrow(new NoSuchFrameException("No frame element found by name or id paymentFrame")).when(webdriver).findElement(any());
    assertThat(new FrameByIdOrName("paymentFrame").apply(webdriver)).isNull();
  }

  @Test
  void apply_shouldIgnore_noSuchElementException() {
    WebDriver webdriver = mock();
    doThrow(new NoSuchElementException("no such element: Unable to locate element: ...")).when(webdriver).findElement(any());
    assertThat(new FrameByIdOrName("paymentFrame").apply(webdriver)).isNull();
  }

  @Test
  void apply_switchesToFrame_ifFrameIsFound() {
    WebDriver webdriver = mock();
    WebElement frame = mock();
    TargetLocator targetLocator = mock();
    when(webdriver.switchTo()).thenReturn(targetLocator);
    when(webdriver.findElement(any())).thenReturn(frame);
    when(targetLocator.frame(any(WebElement.class))).thenReturn(webdriver);

    assertThat(new FrameByIdOrName("paymentFrame").apply(webdriver)).isEqualTo(webdriver);

    verify(webdriver).switchTo();
    verify(targetLocator).frame(frame);
  }
}
