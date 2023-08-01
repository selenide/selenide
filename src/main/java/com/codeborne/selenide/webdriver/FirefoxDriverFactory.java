package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@ParametersAreNonnullByDefault
public class FirefoxDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(FirefoxDriverFactory.class);

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    SessionNotCreatedException failure = null;
    for (int retries = 0; retries < 5; retries++) {
      try {
        return new FirefoxDriver(createDriverService(config), createCapabilities(config, browser, proxy, browserDownloadsFolder));
      }
      catch (SessionNotCreatedException probablyPortAlreadyUsed) {
        log.error("Failed to start firefox", probablyPortAlreadyUsed);
        failure = probablyPortAlreadyUsed;
      }
    }
    throw failure;
  }

  @CheckReturnValue
  @Nonnull
  protected GeckoDriverService createDriverService(Config config) {
    return withLog(config, new GeckoDriverService.Builder());
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public FirefoxOptions createCapabilities(Config config, Browser browser,
                                           @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    FirefoxOptions initialOptions = new FirefoxOptions();
    setHeadless(config, initialOptions);
    setupBrowserBinary(config, initialOptions);
    setupPreferences(initialOptions);

    setupDownloadsFolder(initialOptions, browserDownloadsFolder);

    final FirefoxOptions options = initialOptions.merge(createCommonCapabilities(new FirefoxOptions(), config, browser, proxy));

    transferFirefoxProfileFromSystemProperties(options)
      .ifPresent(profile -> options.setProfile(profile));

    injectFirefoxPrefs(options);
    return options;
  }

  protected void setHeadless(Config config, FirefoxOptions initialOptions) {
    if (config.headless()) {
      initialOptions.addArguments("-headless");
    }
  }

  protected void setupBrowserBinary(Config config, FirefoxOptions firefoxOptions) {
    if (isNotEmpty(config.browserBinary())) {
      log.info("Using browser binary: {}", config.browserBinary());
      firefoxOptions.setBinary(config.browserBinary());
    }
  }

  protected void setupPreferences(FirefoxOptions firefoxOptions) {
    firefoxOptions.addPreference("network.automatic-ntlm-auth.trusted-uris", "http://,https://");
    firefoxOptions.addPreference("network.automatic-ntlm-auth.allow-non-fqdn", true);
    firefoxOptions.addPreference("network.negotiate-auth.delegation-uris", "http://,https://");
    firefoxOptions.addPreference("network.negotiate-auth.trusted-uris", "http://,https://");
    firefoxOptions.addPreference("network.http.phishy-userpass-length", 255);
    firefoxOptions.addPreference("security.csp.enable", false);
    firefoxOptions.addPreference("network.proxy.no_proxies_on", "");
    firefoxOptions.addPreference("network.proxy.allow_hijacking_localhost", true);
  }

  protected void setupDownloadsFolder(FirefoxOptions firefoxOptions, @Nullable File browserDownloadsFolder) {
    if (browserDownloadsFolder != null) {
      firefoxOptions.addPreference("browser.download.dir", browserDownloadsFolder.getAbsolutePath());
    }
    firefoxOptions.addPreference("browser.helperApps.neverAsk.saveToDisk", popularContentTypes());
    firefoxOptions.addPreference("pdfjs.disabled", true);  // disable the built-in viewer
    firefoxOptions.addPreference("browser.download.folderList", 2); // 0=Desktop, 1=Downloads, 2="reuse last location"
  }

  @CheckReturnValue
  @Nonnull
  protected String popularContentTypes() {
    try {
      return String.join(";", IOUtils.readLines(getClass().getResourceAsStream("/content-types.properties"), UTF_8));
    }
    catch (UncheckedIOException e) {
      return "text/plain;text/csv;application/zip;application/pdf;application/octet-stream;" +
        "application/msword;application/vnd.ms-excel;text/css;text/html";
    }
  }

  @CheckReturnValue
  @Nonnull
  protected Map<String, String> collectFirefoxProfileFromSystemProperties() {
    String prefix = "firefoxprofile.";

    Map<String, String> result = new HashMap<>();
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        result.put(capability, value);
      }
    }

    return result;
  }

  @Nonnull
  @CheckReturnValue
  protected Optional<FirefoxProfile> transferFirefoxProfileFromSystemProperties(FirefoxOptions firefoxOptions) {
    Map<String, String> ffProfile = collectFirefoxProfileFromSystemProperties();
    if (ffProfile.isEmpty()) {
      return Optional.empty();
    }

    FirefoxProfile profile = Optional.ofNullable(firefoxOptions.getProfile()).orElseGet(FirefoxProfile::new);
    for (Map.Entry<String, String> entry : ffProfile.entrySet()) {
      String capability = entry.getKey();
      String value = entry.getValue();
      log.debug("Use {}={}", capability, value);
      setCapability(profile, capability, value);
    }
    return Optional.of(profile);
  }

  protected void setCapability(FirefoxProfile profile, String capability, String value) {
    if (isBoolean(value)) {
      profile.setPreference(capability, parseBoolean(value));
    }
    else if (isInteger(value)) {
      profile.setPreference(capability, parseInt(value));
    }
    else {
      profile.setPreference(capability, value);
    }
  }

  private void injectFirefoxPrefs(FirefoxOptions options) {
    if (options.getCapability("moz:firefoxOptions") != null) {
      Map<String, Map<String, Object>> mozOptions = cast(options.getCapability("moz:firefoxOptions"));

      if (mozOptions.containsKey("prefs")) {
        for (Map.Entry<String, Object> pref : mozOptions.get("prefs").entrySet()) {
          options.addPreference(pref.getKey(), pref.getValue());
        }
      }
    }
  }
}
