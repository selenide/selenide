package com.codeborne.selenide.cli;

import com.codeborne.selenide.SelenideConfig;

/**
 * Maps command-line flags to a {@link SelenideConfig}, mirroring the flag style of the Selenide MCP server.
 */
final class CliConfig {
  private CliConfig() {
  }

  static SelenideConfig toConfig(String[] args) {
    SelenideConfig config = new SelenideConfig();
    for (String arg : args) {
      applyBrowserArg(config, arg);
      applyConnectionArg(config, arg);
      applyPageLoadArg(config, arg);
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
      config.timeout(Long.parseLong(value(arg)));
    }
    else if (arg.startsWith("--polling-interval=")) {
      config.pollingInterval(Long.parseLong(value(arg)));
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
      config.pageLoadTimeout(Long.parseLong(value(arg)));
    }
    else if (arg.startsWith("--reports-folder=")) {
      config.reportsFolder(value(arg));
    }
    else if (arg.startsWith("--downloads-folder=")) {
      config.downloadsFolder(value(arg));
    }
  }

  private static String value(String arg) {
    return arg.substring(arg.indexOf('=') + 1);
  }
}
