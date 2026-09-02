package com.codeborne.selenide;

import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.WebDriver;

import java.time.Instant;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Emulates the browser clock and timezone via CDP (Chromium browsers only).
 * <p>
 * The fixed-time emulation is applied to documents <em>loaded after</em>
 * {@link #setFixedTime(Instant)} (or {@link #setTimezone(String)}) is called.
 * To apply it to the current page, reload it after the call.
 */
public class BrowserClock {
  private static final String FIXED_TIME_SCRIPT_TEMPLATE = """
    (() => {
      const FixedDate = Date;
      const fixedTime = %d;
      function MockDate(...args) {
        if (!new.target) {
          return new FixedDate(fixedTime).toString();
        }
        return Reflect.construct(FixedDate, args.length === 0 ? [fixedTime] : args, new.target);
      }
      Object.setPrototypeOf(MockDate, FixedDate);
      MockDate.prototype = Object.create(FixedDate.prototype);
      Object.defineProperty(MockDate.prototype, 'constructor',
        {value: MockDate, writable: true, configurable: true});
      MockDate.now = () => fixedTime;
      MockDate.parse = FixedDate.parse;
      MockDate.UTC = FixedDate.UTC;
      window.Date = MockDate;
    })();
    """;

  private final Driver driver;
  private final Map<WebDriver, String> fixedTimeScriptIds = new WeakHashMap<>();

  BrowserClock(Driver driver) {
    this.driver = driver;
  }

  public void setTimezone(String timezoneId) {
    WebDriver webDriver = driver.getAndCheckWebDriver();
    if (!(webDriver instanceof HasCdp cdpBrowser)) {
      throw new UnsupportedOperationException("Browser clock emulation is not supported in " + webDriver);
    }
    cdpBrowser.executeCdpCommand("Emulation.setTimezoneOverride", Map.of("timezoneId", timezoneId));
  }

  public void setFixedTime(Instant instant) {
    WebDriver webDriver = driver.getAndCheckWebDriver();
    if (!(webDriver instanceof HasCdp cdpBrowser)) {
      throw new UnsupportedOperationException("Browser clock emulation is not supported in " + webDriver);
    }
    removeFixedTimeScript(webDriver, cdpBrowser);
    Map<String, Object> result = cdpBrowser.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument",
      Map.of("source", fixedTimeScript(instant)));
    fixedTimeScriptIds.put(webDriver, String.valueOf(result.get("identifier")));
  }

  public void reset() {
    if (!driver.hasWebDriverStarted()) {
      return;
    }
    WebDriver webDriver = driver.getAndCheckWebDriver();
    if (!(webDriver instanceof HasCdp cdpBrowser)) {
      throw new UnsupportedOperationException("Browser clock emulation is not supported in " + webDriver);
    }
    removeFixedTimeScript(webDriver, cdpBrowser);
    cdpBrowser.executeCdpCommand("Emulation.setTimezoneOverride", Map.of("timezoneId", ""));
  }

  private void removeFixedTimeScript(WebDriver webDriver, HasCdp cdpBrowser) {
    String fixedTimeScriptId = fixedTimeScriptIds.get(webDriver);
    if (fixedTimeScriptId != null) {
      cdpBrowser.executeCdpCommand("Page.removeScriptToEvaluateOnNewDocument",
        Map.of("identifier", fixedTimeScriptId));
      fixedTimeScriptIds.remove(webDriver);
    }
  }

  private String fixedTimeScript(Instant instant) {
    return FIXED_TIME_SCRIPT_TEMPLATE.replace("%d", Long.toString(instant.toEpochMilli()));
  }
}
