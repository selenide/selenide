package com.codeborne.selenide;

import com.codeborne.selenide.ex.FrameNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class SelenideTargetLocatorTest {
  private final Config config = new SelenideConfig();
  private final WebDriver webdriver = mock(WebDriver.class);
  private final DriverStub driver = new DriverStub(config, new Browser("zopera", true), webdriver, null);
  private final SelenideTargetLocator switchTo = new SelenideTargetLocator(driver);
  private final TargetLocator targetLocator = mock(TargetLocator.class);

  @BeforeEach
  void setUp() {
    when(webdriver.switchTo()).thenReturn(targetLocator);
    when(webdriver.getPageSource()).thenReturn("<html><h42/></html>");
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
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with index: 666");

    verify(targetLocator).frame(666);
  }

  @Test
  void switchToFrame_withTimeout() {
    when(targetLocator.frame(anyInt())).thenThrow(new TimeoutException("ups"));

    assertThatThrownBy(() -> switchTo.frame(666))
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with index: 666");

    verify(targetLocator).frame(666);
  }

  @Test
  void switchToFrame_withWrongIndex_chrome75() {
    when(targetLocator.frame(anyInt())).thenThrow(new org.openqa.selenium.InvalidArgumentException(
      "invalid argument: 'id' out of range\n" +
        "  (Session info: headless chrome=75.0.3770.90)\n"));

    assertThatThrownBy(() -> switchTo.frame(666))
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with index: 666");

    verify(targetLocator).frame(666);
  }

  @Test
  void switchToFrame_withWrongIndex_ff62() {
    when(targetLocator.frame(anyInt())).thenThrow(new org.openqa.selenium.InvalidArgumentException(
      "blah-blah untagged enum FrameId\n" +
        "  (Session info: headless chrome=75.0.3770.90)\n"));

    assertThatThrownBy(() -> switchTo.frame(666))
      .isInstanceOf(FrameNotFoundException.class)
      .hasMessageStartingWith("No frame found with index: 666");

    verify(targetLocator).frame(666);
  }
}
