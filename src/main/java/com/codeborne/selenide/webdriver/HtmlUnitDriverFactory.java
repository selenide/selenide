package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.htmlunit.options.HtmlUnitDriverOptions;

import java.io.File;
import java.util.Map;

import static com.codeborne.selenide.Browsers.HTMLUNIT;
import static org.openqa.selenium.remote.CapabilityType.BROWSER_NAME;

/**
 * Creates HtmlUnit driver - a headless browser written in Java.
 * <p>
 *   Requires dependency {@code org.seleniumhq.selenium:htmlunit3-driver} to be added to the project.
 * </p>
 * <p>
 *   HtmlUnit is always headless and doesn't support browser size and position.
 * </p>
 */
public class HtmlUnitDriverFactory extends AbstractDriverFactory {
  @Override
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    return new HtmlUnitDriver(createCapabilities(config, browser, proxy, browserDownloadsFolder));
  }

  @Override
  public HtmlUnitDriverOptions createCapabilities(Config config, Browser browser,
                                                  @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    MutableCapabilities capabilities = new MutableCapabilities(Map.of(BROWSER_NAME, HTMLUNIT));
    HtmlUnitDriverOptions options = new HtmlUnitDriverOptions(createCommonCapabilities(capabilities, config, browser, proxy));
    options.setJavaScriptEnabled(true);
    return options;
  }

  @Override
  public void setBrowserSize(Config config, WebDriver webdriver) {
  }

  @Override
  public void setBrowserPosition(Config config, WebDriver webdriver) {
  }
}
