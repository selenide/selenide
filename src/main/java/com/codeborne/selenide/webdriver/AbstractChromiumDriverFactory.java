package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Config;
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

/**
 * @since 6.0.0
 */
@ParametersAreNonnullByDefault
public abstract class AbstractChromiumDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(AbstractChromiumDriverFactory.class);

  // Regexp from https://stackoverflow.com/a/15739087/1110503 to handle commas in values
  private static final Pattern REGEX_COMMAS_IN_VALUES = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
  private static final Pattern REGEX_REMOVE_QUOTES = Pattern.compile("\"", Pattern.LITERAL);

  @Nonnull
  @CheckReturnValue
  protected List<String> createChromiumArguments(Config config, String externalArguments) {
    List<String> arguments = new ArrayList<>();
    arguments.add("--proxy-bypass-list=<-loopback>");
    arguments.add("--disable-dev-shm-usage");
    arguments.addAll(parseArguments(externalArguments));
    arguments.addAll(createHeadlessArguments(config));
    if (config.browserSize() != null && BrowserResizer.isValidDimension(config.browserSize())) {
      arguments.add(convertBrowserSizeToChromeFormat(config.browserSize()));
    }
    return arguments;
  }

  @Nonnull
  @CheckReturnValue
  protected Map<String, Object> prefs(@Nullable File browserDownloadsFolder, String externalPreferences) {
    Map<String, Object> preferences = new HashMap<>();
    preferences.put("safebrowsing.enabled", true);
    preferences.put("credentials_enable_service", false);
    preferences.put("plugins.always_open_pdf_externally", true);
    preferences.put("profile.default_content_setting_values.automatic_downloads", 1);

    if (browserDownloadsFolder != null) {
      preferences.put("download.default_directory", browserDownloadsFolder.getAbsolutePath());
    }
    preferences.putAll(parsePreferencesFromString(externalPreferences));

    log.debug("Using chromium preferences: {}", preferences);
    return preferences;
  }

  @Nonnull
  @CheckReturnValue
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

  @Nonnull
  @CheckReturnValue
  private String convertBrowserSizeToChromeFormat(String browserSize) {
    return "--window-size=" + browserSize.replace("x", ",");
  }

  @CheckReturnValue
  @Nonnull
  protected Map<String, Object> parsePreferencesFromString(String preferencesString) {
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

  @CheckReturnValue
  @Nonnull
  private String removeQuotes(String value) {
    return REGEX_REMOVE_QUOTES.matcher(value).replaceAll(quoteReplacement(""));
  }
}
