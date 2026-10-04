package com.codeborne.selenide.cli;

import com.codeborne.selenide.SelenideConfig;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.json.JsonException;

import java.util.regex.Pattern;

/**
 * Maps command-line flags to a {@link SelenideConfig}, mirroring the flag style of the Selenide MCP server.
 */
final class CliConfig {
  private static final Pattern REGEX_INTEGER = Pattern.compile("-?\\d{1,9}");

  private CliConfig() {
  }

  static SelenideConfig toConfig(String[] args) {
    SelenideConfig config = new SelenideConfig();
    for (String arg : args) {
      applyBrowserArg(config, arg);
      applyConnectionArg(config, arg);
      applyPageLoadArg(config, arg);
      applyCapabilityArg(config, arg);
    }
    return config;
  }

  private static void applyBrowserArg(SelenideConfig config, String arg) {
    if (arg.startsWith("--browser=")) {
      config.browser(value(arg));
    }
    else if (arg.startsWith("--browser-version=")) {
      config.browserVersion(value(arg));
    }
    else if (arg.startsWith("--browser-size=")) {
      config.browserSize(value(arg));
    }
    else if (arg.startsWith("--browser-binary=")) {
      config.browserBinary(value(arg));
    }
    else if (arg.startsWith("--browser-position=")) {
      config.browserPosition(value(arg));
    }
    else if (arg.equals("--headless")) {
      config.headless(true);
    }
  }

  private static void applyConnectionArg(SelenideConfig config, String arg) {
    if (arg.startsWith("--base-url=")) {
      config.baseUrl(value(arg));
    }
    else if (arg.startsWith("--timeout=")) {
      config.timeout(longValue(arg));
    }
    else if (arg.startsWith("--polling-interval=")) {
      config.pollingInterval(longValue(arg));
    }
    else if (arg.startsWith("--remote=")) {
      config.remote(value(arg));
    }
  }

  private static void applyPageLoadArg(SelenideConfig config, String arg) {
    if (arg.startsWith("--page-load-strategy=")) {
      config.pageLoadStrategy(value(arg));
    }
    else if (arg.startsWith("--page-load-timeout=")) {
      config.pageLoadTimeout(longValue(arg));
    }
    else if (arg.startsWith("--reports-folder=")) {
      config.reportsFolder(value(arg));
    }
    else if (arg.startsWith("--downloads-folder=")) {
      config.downloadsFolder(value(arg));
    }
  }

  /**
   * Handles {@code --capability=<name>=<value>}.
   * Value can be a JSON object or array (e.g. {@code goog:chromeOptions={"args":["--no-sandbox"]}}),
   * a boolean, an integer or a plain string.
   */
  private static void applyCapabilityArg(SelenideConfig config, String arg) {
    if (arg.startsWith("--capability=")) {
      String[] nameAndValue = value(arg).split("=", 2);
      if (nameAndValue.length < 2 || nameAndValue[0].isEmpty()) {
        throw new IllegalArgumentException("Expected --capability=<name>=<value>, but received: " + arg);
      }
      String name = nameAndValue[0];
      config.browserCapabilities().setCapability(name, parseCapabilityValue(name, nameAndValue[1]));
    }
  }

  private static Object parseCapabilityValue(String name, String value) {
    if (value.startsWith("{") || value.startsWith("[")) {
      return parseJson(name, value);
    }
    if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
      return Boolean.valueOf(value);
    }
    if (REGEX_INTEGER.matcher(value).matches()) {
      return Integer.valueOf(value);
    }
    return value;
  }

  private static Object parseJson(String name, String value) {
    try {
      return new Json().toType(value, Object.class);
    }
    catch (JsonException e) {
      throw new IllegalArgumentException("Invalid JSON in capability " + name + ": " + value, e);
    }
  }

  private static String value(String arg) {
    return arg.substring(arg.indexOf('=') + 1);
  }

  private static long longValue(String arg) {
    String value = value(arg);
    try {
      return Long.parseLong(value);
    }
    catch (NumberFormatException e) {
      throw new IllegalArgumentException("invalid value for '" + arg.substring(0, arg.indexOf('=')) + "': '" + value + "'");
    }
  }
}
