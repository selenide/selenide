package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

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
  private static final String HTMLUNIT_DRIVER_CLASS = "org.openqa.selenium.htmlunit.HtmlUnitDriver";
  private final String driverClassName;

  public HtmlUnitDriverFactory() {
    this(HTMLUNIT_DRIVER_CLASS);
  }

  HtmlUnitDriverFactory(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  @Override
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    return HtmlUnitDriverBuilder.driver(createCapabilities(config, browser, proxy, browserDownloadsFolder));
  }

  @Override
  public MutableCapabilities createCapabilities(Config config, Browser browser,
                                                @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    verifyHtmlUnitIsAvailable();
    MutableCapabilities capabilities = new MutableCapabilities(Map.of(BROWSER_NAME, HTMLUNIT));
    return HtmlUnitDriverBuilder.options(createCommonCapabilities(capabilities, config, browser, proxy));
  }

  private void verifyHtmlUnitIsAvailable() {
    try {
      Class.forName(driverClassName, false, getClass().getClassLoader());
    }
    catch (ClassNotFoundException e) {
      throw new IllegalStateException(
        "HtmlUnit driver not found. Add dependency \"org.seleniumhq.selenium:htmlunit3-driver\" to your project.", e);
    }
  }

  @Override
  public void setBrowserSize(Config config, WebDriver webdriver) {
  }

  @Override
  public void setBrowserPosition(Config config, WebDriver webdriver) {
  }
}
