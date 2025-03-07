package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.FileNamer;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.openqa.selenium.UnexpectedAlertBehaviour.ACCEPT;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.openqa.selenium.remote.CapabilityType.BROWSER_VERSION;
import static org.openqa.selenium.remote.CapabilityType.PAGE_LOAD_STRATEGY;
import static org.openqa.selenium.remote.CapabilityType.PROXY;
import static org.openqa.selenium.remote.CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR;

public abstract class AbstractDriverFactory implements DriverFactory {
  private static final Logger log = LoggerFactory.getLogger(AbstractDriverFactory.class);
  private static final Pattern REGEX_SIGNED_INTEGER = Pattern.compile("^-?\\d+$");
  private static final Pattern REGEX_VERSION = Pattern.compile("(\\d+)(\\..*)?");
  private final FileNamer fileNamer = new FileNamer();

  protected File webdriverLog(Config config) {
    File logFolder = ensureFolderExists(new File(config.reportsFolder()).getAbsoluteFile());
    String logFileName = String.format("webdriver.%s.log", fileNamer.generateFileName());
    File logFile = new File(logFolder, logFileName).getAbsoluteFile();
    log.info("Write webdriver logs to: {}", logFile);
    return logFile;
  }

  protected <DS extends DriverService, B extends DriverService.Builder<DS, ?>> DS withLog(Config config, B dsBuilder) {
    if (config.webdriverLogsEnabled()) {
      dsBuilder.withLogFile(webdriverLog(config));
    }
    return dsBuilder.build();
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
    capabilities.setCapability(UNHANDLED_PROMPT_BEHAVIOUR, ACCEPT);

    transferCapabilitiesFromSystemProperties(capabilities);

    return merge(capabilities, config.browserCapabilities());
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
