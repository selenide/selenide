package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.FileNamer;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS;
import static org.openqa.selenium.remote.CapabilityType.PAGE_LOAD_STRATEGY;
import static org.openqa.selenium.remote.CapabilityType.PROXY;
import static org.openqa.selenium.remote.CapabilityType.SUPPORTS_ALERTS;
import static org.openqa.selenium.remote.CapabilityType.TAKES_SCREENSHOT;

@ParametersAreNonnullByDefault
public abstract class AbstractDriverFactory implements DriverFactory {
  private static final Logger log = LoggerFactory.getLogger(AbstractDriverFactory.class);
  private static final Pattern REGEX_SIGNED_INTEGER = Pattern.compile("^-?\\d+$");
  private static final Pattern REGEX_VERSION = Pattern.compile("(\\d+)(\\..*)?");
  private final FileNamer fileNamer = new FileNamer();

  @CheckReturnValue
  @Nonnull
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

  @CheckReturnValue
  @Nonnull
  protected MutableCapabilities createCommonCapabilities(Config config, Browser browser, @Nullable Proxy proxy) {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    if (proxy != null) {
      capabilities.setCapability(PROXY, proxy);
    }
    if (config.browserVersion() != null && !config.browserVersion().isEmpty()) {
      capabilities.setVersion(config.browserVersion());
    }
    capabilities.setCapability(PAGE_LOAD_STRATEGY, config.pageLoadStrategy());
    capabilities.setCapability(ACCEPT_SSL_CERTS, true);

    if (browser.supportsInsecureCerts()) {
      capabilities.setCapability(ACCEPT_INSECURE_CERTS, true);
    }
    capabilities.setJavascriptEnabled(true);
    capabilities.setCapability(TAKES_SCREENSHOT, true);
    capabilities.setCapability(SUPPORTS_ALERTS, true);

    transferCapabilitiesFromSystemProperties(capabilities);
    return new MergeableCapabilities(capabilities, config.browserCapabilities());
  }

  protected void transferCapabilitiesFromSystemProperties(DesiredCapabilities currentBrowserCapabilities) {
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
  @CheckReturnValue
  @Nonnull
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

  @CheckReturnValue
  protected boolean isInteger(String value) {
    return REGEX_SIGNED_INTEGER.matcher(value).matches();
  }

  @CheckReturnValue
  protected boolean isBoolean(String value) {
    return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
  }

  @CheckReturnValue
  protected boolean isSystemPropertyNotSet(String key) {
    return isBlank(System.getProperty(key, ""));
  }

  @CheckReturnValue
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
