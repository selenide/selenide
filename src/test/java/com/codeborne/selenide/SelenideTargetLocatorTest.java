package com.codeborne.selenide;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelenideTargetLocatorTest {
  private Config config = new SelenideConfig();
  private WebDriver webdriver = mock(WebDriver.class);
  private SelenideTargetLocator switchTo = new SelenideTargetLocator(config, webdriver);
  private TargetLocator targetLocator = mock(TargetLocator.class);

  @BeforeEach
  void setUp() {
    when(webdriver.switchTo()).thenReturn(targetLocator);
  }

  @Test
  void switchToFrame_byIndex() {
    when(targetLocator.frame(anyInt())).thenReturn(webdriver);

    assertThat(switchTo.frame(666)).isSameAs(webdriver);

    verify(targetLocator).frame(666);
  }

  @Test
  void switchToFrame_notFound() {
    when(targetLocator.frame(anyInt())).thenThrow(new NoSuchElementException("ups"));

    assertThatThrownBy(() -> switchTo.frame(666))
      .isInstanceOf(NoSuchFrameException.class)
      .hasMessage("No frame found with index: 666");

    verify(targetLocator).frame(666);
  }

  @Test
  void switchToFrame_withTimeout() {
    when(targetLocator.frame(anyInt())).thenThrow(new TimeoutException("ups"));

    assertThatThrownBy(() -> switchTo.frame(666))
      .isInstanceOf(NoSuchFrameException.class)
      .hasMessage("No frame found with index: 666");

    verify(targetLocator).frame(666);
  }

  @Test
  void switchToFrame_withWrongIndex_chrome75() {
    when(targetLocator.frame(anyInt())).thenThrow(new org.openqa.selenium.InvalidArgumentException(
      "invalid argument: 'id' out of range\n" +
        "  (Session info: headless chrome=75.0.3770.90)\n"));

    assertThatThrownBy(() -> switchTo.frame(666))
      .isInstanceOf(NoSuchFrameException.class)
      .hasMessage("No frame found with index: 666");

    verify(targetLocator).frame(666);
  }

  @Test
  void switchToFrame_withWrongIndex_ff62() {
    when(targetLocator.frame(anyInt())).thenThrow(new org.openqa.selenium.InvalidArgumentException(
      "blah-blah untagged enum FrameId\n" +
        "  (Session info: headless chrome=75.0.3770.90)\n"));

    assertThatThrownBy(() -> switchTo.frame(666))
      .isInstanceOf(NoSuchFrameException.class)
      .hasMessage("No frame found with index: 666");

    verify(targetLocator).frame(666);
  }
}
