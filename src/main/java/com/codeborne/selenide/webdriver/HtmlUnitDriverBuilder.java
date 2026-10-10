package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.htmlunit.options.HtmlUnitDriverOptions;

/**
 * The only class referring HtmlUnit classes.
 * <p>
 *   Kept separate from {@link HtmlUnitDriverFactory}, so that the factory can be loaded
 *   (and report a clear error) even if HtmlUnit is missing in classpath.
 * </p>
 */
final class HtmlUnitDriverBuilder {
  private HtmlUnitDriverBuilder() {
  }

  static MutableCapabilities options(Capabilities commonCapabilities) {
    HtmlUnitDriverOptions options = new HtmlUnitDriverOptions(commonCapabilities);
    options.setJavaScriptEnabled(true);
    return options;
  }

  static WebDriver driver(Capabilities capabilities) {
    return new HtmlUnitDriver(capabilities);
  }
}
