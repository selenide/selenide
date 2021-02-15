package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.regex.Matcher.quoteReplacement;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

@ParametersAreNonnullByDefault
public class ChromeDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(ChromeDriverFactory.class);

  // Regexp from https://stackoverflow.com/a/15739087/1110503 to handle commas in values
  private static final Pattern REGEX_COMMAS_IN_VALUES = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
  private static final Pattern REGEX_REMOVE_QUOTES = Pattern.compile("\"", Pattern.LITERAL);

  @Override
  public void setupWebdriverBinary() {
    if (isSystemPropertyNotSet("webdriver.chrome.driver")) {
      WebDriverManager.chromedriver().setup();
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  @SuppressWarnings("deprecation")
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, File browserDownloadsFolder) {
    MutableCapabilities chromeOptions = createCapabilities(config, browser, proxy, browserDownloadsFolder);
    log.debug("Chrome options: {}", chromeOptions);
    return new ChromeDriver(buildService(config), chromeOptions);
  }

  @CheckReturnValue
  @Nonnull
  protected ChromeDriverService buildService(Config config) {
    return withLog(config, new ChromeDriverService.Builder());
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public MutableCapabilities createCapabilities(Config config, Browser browser,
                                                @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    Capabilities commonCapabilities = createCommonCapabilities(config, browser, proxy);

    ChromeOptions options = new ChromeOptions();
    options.setHeadless(config.headless());
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      options.setBinary(config.browserBinary());
    }
    options.addArguments(createChromeArguments(config, browser));
    options.setExperimentalOption("excludeSwitches", excludeSwitches(commonCapabilities));
    options.setExperimentalOption("prefs", prefs(browserDownloadsFolder));
    setMobileEmulation(options);

    return options.merge(commonCapabilities);
  }

  @CheckReturnValue
  @Nonnull
  protected List<String> createChromeArguments(Config config, Browser browser) {
    List<String> arguments = new ArrayList<>();
    arguments.add("--proxy-bypass-list=<-loopback>");
    arguments.add("--disable-dev-shm-usage");
    arguments.add("--no-sandbox");
    arguments.addAll(parseArguments(System.getProperty("chromeoptions.args")));
    arguments.addAll(createHeadlessArguments(config));
    return arguments;
  }

  @CheckReturnValue
  @Nonnull
  protected List<String> createHeadlessArguments(Config config) {
    List<String> arguments = new ArrayList<>();
    if (config.headless()) {
      arguments.add("--disable-background-networking");
      arguments.add("--enable-features=NetworkService,NetworkServiceInProcess");
      arguments.add("--disable-background-timer-throttling");
      arguments.add("--disable-backgrounding-occluded-windows");
      arguments.add("--disable-breakpad");
      arguments.add("--disable-client-side-phishing-detection");
      arguments.add("--disable-component-extensions-with-background-pages");
      arguments.add("--disable-default-apps");
      arguments.add("--disable-features=TranslateUI");
      arguments.add("--disable-hang-monitor");
      arguments.add("--disable-ipc-flooding-protection");
      arguments.add("--disable-popup-blocking");
      arguments.add("--disable-prompt-on-repost");
      arguments.add("--disable-renderer-backgrounding");
      arguments.add("--disable-sync");
      arguments.add("--force-color-profile=srgb");
      arguments.add("--metrics-recording-only");
      arguments.add("--no-first-run");
      arguments.add("--password-store=basic");
      arguments.add("--use-mock-keychain");
      arguments.add("--hide-scrollbars");
      arguments.add("--mute-audio");
    }
    return arguments;
  }

  @CheckReturnValue
  @Nonnull
  protected String[] excludeSwitches(Capabilities capabilities) {
    return hasExtensions(capabilities) ?
      new String[]{"enable-automation"} :
      new String[]{"enable-automation", "load-extension"};
  }

  private boolean hasExtensions(Capabilities capabilities) {
    Map<?, ?> chromeOptions = (Map<?, ?>) capabilities.getCapability("goog:chromeOptions");
    if (chromeOptions == null) return false;

    List<?> extensions = (List<?>) chromeOptions.get("extensions");
    return extensions != null && !extensions.isEmpty();
  }

  private void setMobileEmulation(ChromeOptions chromeOptions) {
    Map<String, Object> mobileEmulation = mobileEmulation();
    if (!mobileEmulation.isEmpty()) {
      chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
    }
  }

  @CheckReturnValue
  @Nonnull
  protected Map<String, Object> mobileEmulation() {
    String mobileEmulation = System.getProperty("chromeoptions.mobileEmulation", "");
    return parsePreferencesFromString(mobileEmulation);
  }

  @CheckReturnValue
  @Nonnull
  protected Map<String, Object> prefs(@Nullable File browserDownloadsFolder) {
    Map<String, Object> chromePreferences = new HashMap<>();
    chromePreferences.put("credentials_enable_service", false);
    chromePreferences.put("plugins.always_open_pdf_externally", true);
    chromePreferences.put("profile.default_content_setting_values.automatic_downloads", 1);

    if (browserDownloadsFolder != null) {
      chromePreferences.put("download.default_directory", browserDownloadsFolder.getAbsolutePath());
    }
    chromePreferences.putAll(parsePreferencesFromString(System.getProperty("chromeoptions.prefs", "")));

    log.debug("Using chrome preferences: {}", chromePreferences);
    return chromePreferences;
  }

  @CheckReturnValue
  @Nonnull
  private Map<String, Object> parsePreferencesFromString(String preferencesString) {
    Map<String, Object> prefs = new HashMap<>();
    List<String> allPrefs = parseCSV(preferencesString);
    for (String pref : allPrefs) {
      String[] keyValue = removeQuotes(pref).split("=");

      if (keyValue.length == 1) {
        log.warn("Missing '=' sign while parsing <key=value> pairs from {}. Key '{}' is ignored.",
          preferencesString, keyValue[0]);
        continue;
      } else if (keyValue.length > 2) {
        log.warn("More than one '=' sign while parsing <key=value> pairs from {}. Key '{}' is ignored.",
          preferencesString, keyValue[0]);
        continue;
      }

      Object prefValue = convertStringToNearestObjectType(keyValue[1]);
      prefs.put(keyValue[0], prefValue);
    }
    return prefs;
  }

  @CheckReturnValue
  @Nonnull
  private List<String> parseArguments(String arguments) {
    return parseCSV(arguments).stream()
      .map(this::removeQuotes)
      .collect(toList());
  }

  @CheckReturnValue
  @Nonnull
  private String removeQuotes(String value) {
    return REGEX_REMOVE_QUOTES.matcher(value).replaceAll(quoteReplacement(""));
  }

  /**
   * parse parameters which can come from command-line interface
   * @param csvString comma-separated values, quotes can be used to mask spaces and commas
   *                  Example: 123,"foo bar","bar,foo"
   * @return values as array, quotes are preserved
   */
  @CheckReturnValue
  @Nonnull
  final List<String> parseCSV(String csvString) {
    return isBlank(csvString) ? emptyList() : asList(REGEX_COMMAS_IN_VALUES.split(csvString));
  }
}
