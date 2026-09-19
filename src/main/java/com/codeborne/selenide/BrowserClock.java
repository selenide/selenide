package com.codeborne.selenide;

import com.codeborne.selenide.impl.FileContent;
import org.jspecify.annotations.Nullable;
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
  private static final FileContent MOCK_DATE_SCRIPT = new FileContent("mock-date.js");

  private final Driver driver;

  /**
   * The clock only ever tracks the single script installed on the driver's <em>current</em>
   * WebDriver - not a map of all WebDrivers ever seen. If the browser was reopened (e.g.
   * reopenBrowserOnFail) since this was set, its WebDriver reference is stale; the next call
   * that finds a mismatch drops it immediately rather than leaving it to be garbage-collected.
   */
  private @Nullable InstalledScript installedScript;

  BrowserClock(Driver driver) {
    this.driver = driver;
  }

  public void setTimezone(String timezoneId) {
    requireNonNull(timezoneId, "timezoneId must not be null");
    WebDriver webDriver = driver.getWebDriver();
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
    WebDriver webDriver = driver.getWebDriver();
    removeFixedTimeScript(webDriver);
    if (webDriver instanceof HasCdp cdpBrowser) {
      Map<String, Object> result = cdpBrowser.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument",
        Map.of("source", "(" + mockDateScript(epochMilli) + ")();"));
      installedScript = new InstalledScript(webDriver, false, String.valueOf(result.get("identifier")));
    }
    else if (isBiDiEnabled(webDriver)) {
      try (Script script = new Script(webDriver.getWindowHandle(), webDriver)) {
        String scriptId = script.addPreloadScript(mockDateScript(epochMilli));
        installedScript = new InstalledScript(webDriver, true, scriptId);
      }
    }
    else {
      throw new UnsupportedOperationException("Browser clock emulation is not supported in " + webDriver);
    }
  }

  public void reset() {
    if (!driver.hasWebDriverStarted()) {
      return;
    }
    WebDriver webDriver = driver.getWebDriver();
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
  private void setTimezoneOverride(WebDriver webDriver, @Nullable String timezoneId) {
    BiDi biDi = ((HasBiDi) webDriver).getBiDi();
    Map<String, @Nullable Object> params = new HashMap<>();
    params.put("timezone", timezoneId);
    params.put("contexts", List.of(webDriver.getWindowHandle()));
    biDi.send(new Command<>("emulation.setTimezoneOverride", params));
  }

  private void removeFixedTimeScript(WebDriver webDriver) {
    InstalledScript installed = installedScript;
    if (installed != null && installed.webDriver() == webDriver) {
      if (installed.viaBiDi()) {
        try (Script script = new Script(webDriver)) {
          script.removePreloadScript(installed.id());
        }
      }
      else {
        ((HasCdp) webDriver).executeCdpCommand("Page.removeScriptToEvaluateOnNewDocument",
          Map.of("identifier", installed.id()));
      }
    }
    installedScript = null;
  }

  private long toEpochMilli(Instant instant) {
    try {
      return instant.toEpochMilli();
    }
    catch (ArithmeticException overflow) {
      throw new IllegalArgumentException("Instant is out of range for browser clock emulation: " + instant, overflow);
    }
  }

  /**
   * mock-date.js evaluates to a function of {@code fixedTime} that itself returns the actual
   * preload script (a function with no arguments). Both CDP and BiDi call the produced preload
   * script automatically on every new document; neither of them accepts extra call arguments,
   * so this calls the loaded function with {@code epochMilli} right away, and what's returned -
   * the actual, zero-argument preload script, with {@code fixedTime} captured via closure rather
   * than templated into the source text - is what gets registered as the preload script.
   */
  static String mockDateScript(long epochMilli) {
    return "(%s)(%d)".formatted(MOCK_DATE_SCRIPT.content(), epochMilli);
  }

  private record InstalledScript(WebDriver webDriver, boolean viaBiDi, String id) {
  }
}
