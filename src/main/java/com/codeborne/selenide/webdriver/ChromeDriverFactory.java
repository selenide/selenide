package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
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
    return new ChromeDriverService.Builder()
      .withLogFile(webdriverLog(config))
      .build();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public MutableCapabilities createCapabilities(Config config, Browser browser, @Nullable Proxy proxy, File browserDownloadsFolder) {
    ChromeOptions options = new ChromeOptions();
    options.setHeadless(config.headless());
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      options.setBinary(config.browserBinary());
    }
    options.addArguments(createChromeArguments(config, browser));
    options.setExperimentalOption("excludeSwitches", excludeSwitches());
    options.setExperimentalOption("prefs", prefs(config, browserDownloadsFolder));
    setMobileEmulation(config, options);

    return new MergeableCapabilities(options, createCommonCapabilities(config, browser, proxy));
  }

  @CheckReturnValue
  @Nonnull
  protected List<String> createChromeArguments(Config config, Browser browser) {
    List<String> arguments = new ArrayList<>();
    arguments.add("--proxy-bypass-list=<-loopback>");
    arguments.add("--disable-dev-shm-usage");
    arguments.add("--no-sandbox");
    arguments.addAll(parseArguments(System.getProperty("chromeoptions.args")));
    return arguments;
  }

  @CheckReturnValue
  @Nonnull
  protected String[] excludeSwitches() {
    return new String[]{"enable-automation", "load-extension"};
  }

  private void setMobileEmulation(Config config, ChromeOptions chromeOptions) {
    Map<String, Object> mobileEmulation = mobileEmulation(config);
    if (!mobileEmulation.isEmpty()) {
      chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
    }
  }

  @CheckReturnValue
  @Nonnull
  protected Map<String, Object> mobileEmulation(Config config) {
    String mobileEmulation = System.getProperty("chromeoptions.mobileEmulation", "");
    return parsePreferencesFromString(mobileEmulation);
  }

  @CheckReturnValue
  @Nonnull
  protected Map<String, Object> prefs(Config config, File browserDownloadsFolder) {
    Map<String, Object> chromePreferences = new HashMap<>();
    chromePreferences.put("credentials_enable_service", false);
    chromePreferences.put("plugins.always_open_pdf_externally", true);

    if (config.remote() == null) {
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
