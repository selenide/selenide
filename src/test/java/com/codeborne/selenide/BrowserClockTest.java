package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chromium.HasCdp;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class BrowserClockTest {
  private final Driver driver = mock();
  private final SelenideDriver selenideDriver = new SelenideDriver(new SelenideConfig(), driver);

  @Test
  void setTimezone_sendsCdpTimezoneOverride() {
    ChromiumDriver webDriver = mock();
    when(driver.getAndCheckWebDriver()).thenReturn(webDriver);

    selenideDriver.clock().setTimezone("America/New_York");

    verify(webDriver).executeCdpCommand(eq("Emulation.setTimezoneOverride"),
      eq(Map.of("timezoneId", "America/New_York")));
  }

  @Test
  void setTimezone_throwsUnsupportedOperationExceptionOnNonCdpDriver() {
    when(driver.getAndCheckWebDriver()).thenReturn(mock(WebDriver.class));

    assertThatThrownBy(() -> selenideDriver.clock().setTimezone("America/New_York"))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageContaining("Browser clock emulation is not supported");
  }

  @Test
  void setFixedTime_addsScriptToEvaluateOnNewDocument() {
    ChromiumDriver webDriver = mock();
    when(driver.getAndCheckWebDriver()).thenReturn(webDriver);

    selenideDriver.clock().setFixedTime(Instant.parse("2025-01-15T14:00:00Z"));

    verify(webDriver).executeCdpCommand(eq("Page.addScriptToEvaluateOnNewDocument"), any());
  }

  @Test
  void setFixedTime_containsFixedInstant() {
    ChromiumDriver webDriver = mock();
    when(driver.getAndCheckWebDriver()).thenReturn(webDriver);
    AtomicReference<Map<String, Object>> commandParams = captureAddedScriptParams(webDriver);

    selenideDriver.clock().setFixedTime(Instant.parse("2025-01-15T14:00:00Z"));

    assertThat(String.valueOf(commandParams.get().get("source"))).contains("1736949600000");
  }

  @Test
  void setFixedTime_scriptPreservesDateBehaviour() {
    ChromiumDriver webDriver = mock();
    when(driver.getAndCheckWebDriver()).thenReturn(webDriver);
    AtomicReference<Map<String, Object>> commandParams = captureAddedScriptParams(webDriver);

    selenideDriver.clock().setFixedTime(Instant.parse("2025-01-15T14:00:00Z"));

    String script = String.valueOf(commandParams.get().get("source"));
    assertThat(script)
      .contains("function MockDate(...args)")
      .contains("if (!new.target)")
      .contains("Reflect.construct(FixedDate")
      .contains("MockDate.now = () => fixedTime")
      .contains("MockDate.parse = FixedDate.parse")
      .contains("MockDate.UTC = FixedDate.UTC")
      .contains("window.Date = MockDate");
  }

  @Test
  void setFixedTime_replacesPreviousScript() {
    ChromiumDriver webDriver = mock();
    when(driver.getAndCheckWebDriver()).thenReturn(webDriver);
    when(webDriver.executeCdpCommand(eq("Page.addScriptToEvaluateOnNewDocument"), any()))
      .thenReturn(Map.of("identifier", "script-1"), Map.of("identifier", "script-2"));

    selenideDriver.clock().setFixedTime(Instant.parse("2025-01-15T14:00:00Z"));
    selenideDriver.clock().setFixedTime(Instant.parse("2025-02-15T14:00:00Z"));

    verify(webDriver).executeCdpCommand(eq("Page.removeScriptToEvaluateOnNewDocument"),
      eq(Map.of("identifier", "script-1")));
  }

  @Test
  void setFixedTime_throwsUnsupportedOperationExceptionOnNonCdpDriver() {
    when(driver.getAndCheckWebDriver()).thenReturn(mock(WebDriver.class));

    assertThatThrownBy(() -> selenideDriver.clock().setFixedTime(Instant.parse("2025-01-15T14:00:00Z")))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageContaining("Browser clock emulation is not supported");
  }

  @Test
  void reset_clearsTimezoneAndRemovesStoredScript() {
    ChromiumDriver webDriver = mock();
    when(driver.hasWebDriverStarted()).thenReturn(true);
    when(driver.getAndCheckWebDriver()).thenReturn(webDriver);
    when(webDriver.executeCdpCommand(eq("Page.addScriptToEvaluateOnNewDocument"), any()))
      .thenReturn(Map.of("identifier", "script-1"));

    selenideDriver.clock().setFixedTime(Instant.parse("2025-01-15T14:00:00Z"));
    selenideDriver.clock().reset();

    verify(webDriver).executeCdpCommand(eq("Emulation.setTimezoneOverride"),
      eq(Map.of("timezoneId", "")));
    verify(webDriver).executeCdpCommand(eq("Page.removeScriptToEvaluateOnNewDocument"),
      eq(Map.of("identifier", "script-1")));
  }

  @Test
  void reset_isIdempotentWhenNoFixedTimeSet() {
    ChromiumDriver webDriver = mock();
    when(driver.hasWebDriverStarted()).thenReturn(true);
    when(driver.getAndCheckWebDriver()).thenReturn(webDriver);

    selenideDriver.clock().reset();

    verify(webDriver).executeCdpCommand(eq("Emulation.setTimezoneOverride"),
      eq(Map.of("timezoneId", "")));
    verify(webDriver, never()).executeCdpCommand(eq("Page.removeScriptToEvaluateOnNewDocument"), any());
  }

  @Test
  void clockState_isNotSharedBetweenDrivers() {
    ChromiumDriver firstWebDriver = mock();
    when(driver.getAndCheckWebDriver()).thenReturn(firstWebDriver);
    when(firstWebDriver.executeCdpCommand(eq("Page.addScriptToEvaluateOnNewDocument"), any()))
      .thenReturn(Map.of("identifier", "script-1"));
    Driver secondDriver = mock();
    ChromiumDriver secondWebDriver = mock();
    when(secondDriver.hasWebDriverStarted()).thenReturn(true);
    when(secondDriver.getAndCheckWebDriver()).thenReturn(secondWebDriver);

    SelenideDriver firstSelenideDriver = new SelenideDriver(new SelenideConfig(), driver);
    SelenideDriver secondSelenideDriver = new SelenideDriver(new SelenideConfig(), secondDriver);
    firstSelenideDriver.clock().setFixedTime(Instant.parse("2025-01-15T14:00:00Z"));

    secondSelenideDriver.clock().reset();

    verify(secondWebDriver, never()).executeCdpCommand(eq("Page.removeScriptToEvaluateOnNewDocument"), any());
  }

  @Test
  void reset_doesNotRemoveScriptInstalledOnAnotherBrowser() {
    ChromiumDriver firstWebDriver = mock();
    when(driver.getAndCheckWebDriver()).thenReturn(firstWebDriver);
    when(firstWebDriver.executeCdpCommand(eq("Page.addScriptToEvaluateOnNewDocument"), any()))
      .thenReturn(Map.of("identifier", "script-1"));
    ChromiumDriver secondWebDriver = mock();
    when(driver.hasWebDriverStarted()).thenReturn(true);

    selenideDriver.clock().setFixedTime(Instant.parse("2025-01-15T14:00:00Z"));
    when(driver.getAndCheckWebDriver()).thenReturn(secondWebDriver);

    selenideDriver.clock().reset();

    verify(secondWebDriver, never()).executeCdpCommand(eq("Page.removeScriptToEvaluateOnNewDocument"), any());
  }

  @Test
  void reset_doesNotStartBrowserIfNotStarted() {
    when(driver.hasWebDriverStarted()).thenReturn(false);

    selenideDriver.clock().reset();

    verify(driver, never()).getAndCheckWebDriver();
    verify(driver, never()).getWebDriver();
  }

  @Test
  void reset_retriesRemovingScriptWhenCdpCallFails() {
    ChromiumDriver webDriver = mock();
    when(driver.hasWebDriverStarted()).thenReturn(true);
    when(driver.getAndCheckWebDriver()).thenReturn(webDriver);
    when(webDriver.executeCdpCommand(eq("Page.addScriptToEvaluateOnNewDocument"), any()))
      .thenReturn(Map.of("identifier", "script-1"));
    selenideDriver.clock().setFixedTime(Instant.parse("2025-01-15T14:00:00Z"));

    when(webDriver.executeCdpCommand(eq("Page.removeScriptToEvaluateOnNewDocument"), any()))
      .thenThrow(new WebDriverException("transient failure"))
      .thenReturn(Map.of());

    assertThatThrownBy(() -> selenideDriver.clock().reset())
      .isInstanceOf(WebDriverException.class);
    selenideDriver.clock().reset();

    verify(webDriver, times(2)).executeCdpCommand(eq("Page.removeScriptToEvaluateOnNewDocument"),
      eq(Map.of("identifier", "script-1")));
  }

  private interface ChromiumDriver extends WebDriver, HasCdp {
  }

  private AtomicReference<Map<String, Object>> captureAddedScriptParams(ChromiumDriver webDriver) {
    AtomicReference<Map<String, Object>> commandParams = new AtomicReference<>();
    doAnswer(invocation -> {
      commandParams.set(invocation.getArgument(1));
      return Map.of("identifier", "script-1");
    }).when(webDriver).executeCdpCommand(eq("Page.addScriptToEvaluateOnNewDocument"), any());
    return commandParams;
  }
}
