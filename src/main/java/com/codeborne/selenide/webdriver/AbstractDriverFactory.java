package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.AttachmentHandler;
import com.codeborne.selenide.impl.FileNamer;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.service.DriverFinder;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.openqa.selenium.UnexpectedAlertBehaviour.IGNORE;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.openqa.selenium.remote.CapabilityType.BROWSER_VERSION;
import static org.openqa.selenium.remote.CapabilityType.PAGE_LOAD_STRATEGY;
import static org.openqa.selenium.remote.CapabilityType.PROXY;
import static org.openqa.selenium.remote.CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR;

public abstract class AbstractDriverFactory implements DriverFactory {
  private static final Logger log = LoggerFactory.getLogger(AbstractDriverFactory.class);
  private static final Pattern REGEX_SIGNED_INTEGER = Pattern.compile("^-?\\d+$");
  private static final Pattern REGEX_VERSION = Pattern.compile("(\\d+)(\\..*)?");
  private static final List<String> VENDOR_OPTIONS = List.of("selenoid:options", "moon:options");
  private final FileNamer fileNamer = new FileNamer();
  private final AttachmentHandler attachmentHandler = inject();

  protected File webdriverLog(Config config) {
    File logFolder = ensureFolderExists(new File(config.reportsFolder()).getAbsoluteFile());
    String logFileName = String.format("webdriver.%s.log", fileNamer.generateFileName());
    File logFile = new File(logFolder, logFileName).getAbsoluteFile();
    log.info("Write webdriver logs to: {}", logFile);
    attachmentHandler.attach(logFile);
    return logFile;
  }

  /**
   * Selenium passes the browser's proxy to Selenium Manager, so Selenium Manager would go through Selenide's MITM proxy
   * to download drivers/browsers. Selenium Manager doesn't trust the MITM certificate (since 4.52.0 it even breaks its json output).
   * So we find the driver (and browser) without Selenide proxy, and Selenium will not run Selenium Manager anymore.
   * <p>
   * Any other proxy (e.g. a corporate proxy set by user in browser capabilities) is still passed to Selenium Manager.
   * <p>
   * See <a href="https://github.com/SeleniumHQ/selenium/issues/18263">Selenium issue 18263</a>
   */
  protected void findBinariesWithoutSelenideProxy(@Nullable Proxy selenideProxy, DriverService service,
                                                  MutableCapabilities options, Consumer<String> setBrowserBinary) {
    if (selenideProxy == null) return;

    MutableCapabilities optionsWithoutProxy = new MutableCapabilities(options);
    optionsWithoutProxy.setCapability(PROXY, (Object) null);
    DriverFinder finder = driverFinder(service, optionsWithoutProxy);
    service.setExecutable(finder.getDriverPath());
    if (finder.hasBrowserPath()) {
      setBrowserBinary.accept(finder.getBrowserPath());
      options.setCapability(BROWSER_VERSION, (Object) null);
    }
  }

  DriverFinder driverFinder(DriverService service, Capabilities options) {
    return new DriverFinder(service, options);
  }

  protected <DS extends DriverService, B extends DriverService.Builder<DS, ?>> DS withLog(Config config, B dsBuilder) {
    if (config.webdriverLogsEnabled()) {
      dsBuilder.withLogFile(webdriverLog(config));
    }
    setTimezone(config, dsBuilder);
    return dsBuilder.build();
  }

  /**
   * The browser inherits environment variables from the webdriver process,
   * so the whole browser (all tabs, windows and workers) uses this timezone.
   */
  protected void setTimezone(Config config, DriverService.Builder<?, ?> dsBuilder) {
    String timezone = timezone(config);
    if (timezone == null) return;

    if (isWindows()) {
      log.warn("Browsers on Windows ignore environment variable TZ, so timezone {} is not applied", timezone);
      return;
    }
    log.info("Set browser timezone to {}", timezone);
    dsBuilder.withEnvironment(Map.of("TZ", timezone));
  }

  protected boolean isWindows() {
    return IS_OS_WINDOWS;
  }

  protected MutableCapabilities createCommonCapabilities(Config config, Browser browser, @Nullable Proxy proxy) {
    return createCommonCapabilities(new MutableCapabilities(), config, browser, proxy);
  }

  protected <T extends MutableCapabilities> T createCommonCapabilities(T capabilities,
                                                                       Config config,
                                                                       Browser browser,
                                                                       @Nullable Proxy proxy) {
    if (proxy != null) {
      capabilities.setCapability(PROXY, proxy);
    }
    String browserVersion = config.browserVersion();
    if (browserVersion != null && !browserVersion.isEmpty()) {
      capabilities.setCapability(BROWSER_VERSION, browserVersion);
    }
    capabilities.setCapability(PAGE_LOAD_STRATEGY, config.pageLoadStrategy());

    if (browser.supportsInsecureCerts()) {
      capabilities.setCapability(ACCEPT_INSECURE_CERTS, true);
    }
    capabilities.setCapability(UNHANDLED_PROMPT_BEHAVIOUR, IGNORE);
    setRemoteTimezone(config, capabilities);

    transferCapabilitiesFromSystemProperties(capabilities);

    T result = merge(capabilities, config.browserCapabilities());
    setVendorTimezone(config, result);
    return result;
  }

  /**
   * Selenium Grid (in Docker) starts the remote browser with given timezone.
   */
  private void setRemoteTimezone(Config config, MutableCapabilities capabilities) {
    String timezone = timezone(config);
    if (config.remote() != null && timezone != null) {
      capabilities.setCapability("se:timeZone", timezone);
    }
  }

  /**
   * Selenoid and Moon read timezone from their own options, not from "se:timeZone".
   * We only extend these options if they are already given, so that other grids don't receive unknown capabilities.
   */
  private void setVendorTimezone(Config config, MutableCapabilities capabilities) {
    String timezone = timezone(config);
    if (config.remote() == null || timezone == null) return;

    for (String name : VENDOR_OPTIONS) {
      if (capabilities.getCapability(name) instanceof Map<?, ?> options && !options.containsKey("timeZone")) {
        Map<Object, Object> optionsWithTimezone = new HashMap<>(options);
        optionsWithTimezone.put("timeZone", timezone);
        capabilities.setCapability(name, optionsWithTimezone);
      }
    }
  }

  /**
   * Browsers silently fall back to UTC for an unknown timezone, so we fail fast on a typo.
   * <p>
   * Only tz database IDs are accepted (e.g. "Europe/Tallinn", "UTC", "Etc/GMT-2").
   * Offset-style IDs like "GMT+02:00" are rejected: Java reads it as UTC+2, but {@code TZ} reads it as UTC-2.
   */
  protected @Nullable String timezone(Config config) {
    String timezone = config.timezone();
    if (isBlank(timezone)) return null;
    if (!ZoneId.getAvailableZoneIds().contains(timezone)) {
      throw new IllegalArgumentException("Invalid timezone: \"" + timezone + "\". Use a tz database ID like \"Europe/Tallinn\"");
    }
    return timezone;
  }

  @SuppressWarnings("unchecked")
  protected <T extends MutableCapabilities> T merge(T capabilities, MutableCapabilities additionalCapabilities) {
    verifyItsSameBrowser(capabilities, additionalCapabilities);
    return (T) capabilities.merge(additionalCapabilities);
  }

  private void verifyItsSameBrowser(Capabilities base, Capabilities extra) {
    if (areDifferent(base.getBrowserName(), extra.getBrowserName())) {
      throw new IllegalArgumentException(String.format("Conflicting browser name: '%s' vs. '%s'",
        base.getBrowserName(), extra.getBrowserName()));
    }
  }

  private boolean areDifferent(String text1, String text2) {
    return !text1.isEmpty() && !text2.isEmpty() && !text1.equals(text2);
  }

  protected void transferCapabilitiesFromSystemProperties(MutableCapabilities currentBrowserCapabilities) {
    String prefix = "capabilities.";
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        log.debug("Use {}={}", key, value);
        currentBrowserCapabilities.setCapability(capability, convertStringToNearestObjectType(value));
      }
    }
  }

  /**
   * Converts String to Boolean\Integer or returns original String.
   * @param value string to convert
   * @return string's object representation
   */
  protected Object convertStringToNearestObjectType(String value) {
    if (isBoolean(value)) {
      return Boolean.valueOf(value);
    }
    else if (isInteger(value)) {
      return parseInt(value);
    }
    else {
      return value;
    }
  }

  protected boolean isInteger(String value) {
    return REGEX_SIGNED_INTEGER.matcher(value).matches();
  }

  protected boolean isBoolean(String value) {
    return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
  }

  protected int majorVersion(@Nullable String browserVersion) {
    if (isBlank(browserVersion)) return 0;
    Matcher matcher = REGEX_VERSION.matcher(browserVersion);
    return matcher.matches() ? parseInt(matcher.replaceFirst("$1")) : 0;
  }

  @SuppressWarnings("unchecked")
  protected <T> T cast(Object value) {
    return (T) value;
  }
}
