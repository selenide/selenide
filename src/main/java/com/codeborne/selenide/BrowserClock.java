package com.codeborne.selenide;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.BiDi;
import org.openqa.selenium.bidi.Command;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.module.Script;
import org.openqa.selenium.chromium.HasCdp;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static com.codeborne.selenide.impl.BiDiUti.isBiDiEnabled;
import static java.util.Objects.requireNonNull;

/**
 * Emulates the browser clock and timezone.
 * <p>
 * Uses CDP in Chromium browsers (Chrome, Edge), and WebDriver BiDi otherwise (e.g. Firefox).
 * <p>
 * The fixed-time emulation is applied to documents <em>loaded after</em>
 * {@link #setFixedTime(Instant)} (or {@link #setTimezone(String)}) is called.
 * To apply it to the current page, reload it after the call.
 */
public class BrowserClock {
  private static final String FIXED_TIME_FUNCTION_TEMPLATE = """
    () => {
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
    }
    """;

  private final Driver driver;
  private final Map<WebDriver, InstalledScript> installedScripts = new WeakHashMap<>();

  BrowserClock(Driver driver) {
    this.driver = driver;
  }

  public void setTimezone(String timezoneId) {
    requireNonNull(timezoneId, "timezoneId must not be null");
    WebDriver webDriver = driver.getAndCheckWebDriver();
    if (webDriver instanceof HasCdp cdpBrowser) {
      cdpBrowser.executeCdpCommand("Emulation.setTimezoneOverride", Map.of("timezoneId", timezoneId));
    }
    else if (isBiDiEnabled(webDriver)) {
      setTimezoneOverride(webDriver, timezoneId);
    }
    else {
      throw new UnsupportedOperationException("Browser clock emulation is not supported in " + webDriver);
    }
  }

  public void setFixedTime(Instant instant) {
    requireNonNull(instant, "instant must not be null");
    long epochMilli = toEpochMilli(instant);
    WebDriver webDriver = driver.getAndCheckWebDriver();
    removeFixedTimeScript(webDriver);
    if (webDriver instanceof HasCdp cdpBrowser) {
      Map<String, Object> result = cdpBrowser.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument",
        Map.of("source", "(" + fixedTimeFunction(epochMilli) + ")();"));
      installedScripts.put(webDriver, new InstalledScript(false, String.valueOf(result.get("identifier"))));
    }
    else if (isBiDiEnabled(webDriver)) {
      String scriptId = new Script(webDriver).addPreloadScript(fixedTimeFunction(epochMilli));
      installedScripts.put(webDriver, new InstalledScript(true, scriptId));
    }
    else {
      throw new UnsupportedOperationException("Browser clock emulation is not supported in " + webDriver);
    }
  }

  public void reset() {
    if (!driver.hasWebDriverStarted()) {
      return;
    }
    WebDriver webDriver = driver.getAndCheckWebDriver();
    removeFixedTimeScript(webDriver);
    if (webDriver instanceof HasCdp cdpBrowser) {
      cdpBrowser.executeCdpCommand("Emulation.setTimezoneOverride", Map.of("timezoneId", ""));
    }
    else if (isBiDiEnabled(webDriver)) {
      setTimezoneOverride(webDriver, null);
    }
    // else: this browser never supported clock emulation, so there is nothing to reset.
  }

  @SuppressWarnings("removal")
  private void setTimezoneOverride(WebDriver webDriver, String timezoneId) {
    BiDi biDi = ((HasBiDi) webDriver).getBiDi();
    Map<String, Object> params = new HashMap<>();
    params.put("timezone", timezoneId);
    params.put("contexts", List.of(webDriver.getWindowHandle()));
    biDi.send(new Command<>("emulation.setTimezoneOverride", params));
  }

  private void removeFixedTimeScript(WebDriver webDriver) {
    InstalledScript installed = installedScripts.get(webDriver);
    if (installed == null) {
      return;
    }
    if (installed.viaBiDi()) {
      new Script(webDriver).removePreloadScript(installed.id());
    }
    else {
      ((HasCdp) webDriver).executeCdpCommand("Page.removeScriptToEvaluateOnNewDocument",
        Map.of("identifier", installed.id()));
    }
    installedScripts.remove(webDriver);
  }

  private long toEpochMilli(Instant instant) {
    try {
      return instant.toEpochMilli();
    }
    catch (ArithmeticException overflow) {
      throw new IllegalArgumentException("Instant is out of range for browser clock emulation: " + instant, overflow);
    }
  }

  private String fixedTimeFunction(long epochMilli) {
    return FIXED_TIME_FUNCTION_TEMPLATE.replace("%d", Long.toString(epochMilli));
  }

  private record InstalledScript(boolean viaBiDi, String id) {
  }
}
