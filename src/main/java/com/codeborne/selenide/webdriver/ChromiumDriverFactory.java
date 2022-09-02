package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
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
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@ParametersAreNonnullByDefault
public class ChromiumDriverFactory extends AbstractChromiumDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(ChromiumDriverFactory.class);

  @Override
  public void setupWebdriverBinary() {
    if (isSystemPropertyNotSet("webdriver.chrome.driver")) {
      // WebDriverManager.chromedriver().setup();
      WebDriverManager.chromiumdriver().setup();
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    ChromeOptions chromeOptions = createCapabilities(config, browser, proxy, browserDownloadsFolder);
    log.debug("Chromium options: {}", chromeOptions);
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
  public ChromeOptions createCapabilities(Config config, Browser browser,
                                            @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    ChromeOptions commonCapabilities = createCommonCapabilities(new ChromeOptions(), config, browser, proxy);

    ChromeOptions options = new ChromeOptions();
    options.setHeadless(config.headless());
    if (isNotEmpty(config.browserBinary())) {
      log.info("Using browser binary: {}", config.browserBinary());
      options.setBinary(config.browserBinary());
    }
    options.addArguments(createChromeArguments(config, browser));
    options.setExperimentalOption("excludeSwitches", excludeSwitches(commonCapabilities));
    options.setExperimentalOption("prefs", prefs(browserDownloadsFolder, System.getProperty("chromeoptions.prefs", "")));
    setMobileEmulation(options);

    return options.merge(commonCapabilities);
  }

  @CheckReturnValue
  @Nonnull
  protected List<String> createChromeArguments(Config config, Browser browser) {
    return createChromiumArguments(config, System.getProperty("chromeoptions.args"));
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
}
