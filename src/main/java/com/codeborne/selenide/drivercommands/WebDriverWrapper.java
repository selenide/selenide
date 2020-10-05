package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

/**
 * A `Driver` implementation which uses given webdriver [and proxy].
 * It doesn't open a new browser.
 * It doesn't start a new proxy.
 */
@ParametersAreNonnullByDefault
public class WebDriverWrapper implements Driver {
  private static final Logger log = LoggerFactory.getLogger(WebDriverWrapper.class);

  private final Config config;
  private final WebDriver webDriver;
  private final SelenideProxyServer selenideProxy;
  private final DownloadsFolder browserDownloadsFolder;
  private final BrowserHealthChecker browserHealthChecker;
  private final CloseDriverCommand closeDriverCommand;

  public WebDriverWrapper(Config config, WebDriver webDriver,
                          @Nullable SelenideProxyServer selenideProxy, DownloadsFolder browserDownloadsFolder) {
    this(config, webDriver, selenideProxy, browserDownloadsFolder, new BrowserHealthChecker(), new CloseDriverCommand());
  }

  private WebDriverWrapper(Config config, WebDriver webDriver,
                           @Nullable SelenideProxyServer selenideProxy, DownloadsFolder browserDownloadsFolder,
                           BrowserHealthChecker browserHealthChecker, CloseDriverCommand closeDriverCommand) {
    requireNonNull(config, "config must not be null");
    requireNonNull(webDriver, "webDriver must not be null");

    this.config = config;
    this.webDriver = webDriver;
    this.selenideProxy = selenideProxy;
    this.browserDownloadsFolder = browserDownloadsFolder;
    this.browserHealthChecker = browserHealthChecker;
    this.closeDriverCommand = closeDriverCommand;
  }

  @Override
  @Nonnull
  public Config config() {
    return config;
  }

  @Override
  @Nonnull
  public Browser browser() {
    return new Browser(config.browser(), config.headless());
  }

  @Override
  public boolean hasWebDriverStarted() {
    return webDriver != null;
  }

  @Override
  @Nonnull
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  @Nullable
  public SelenideProxyServer getProxy() {
    return selenideProxy;
  }

  @Override
  @Nullable
  public WebDriver getAndCheckWebDriver() {
    if (webDriver != null && !browserHealthChecker.isBrowserStillOpen(webDriver)) {
      log.info("Webdriver has been closed meanwhile");
      close();
      return null;
    }
    return webDriver;
  }

  @Override
  public DownloadsFolder browserDownloadsFolder() {
    return browserDownloadsFolder;
  }

  /**
   * Close the webdriver.
   * <p>
   * NB! The behaviour was changed in Selenide 5.4.0
   * Even if webdriver was created by user - it will be closed.
   * It may hurt if you try to use this browser after closing.
   */
  @Override
  public void close() {
    closeDriverCommand.close(config, webDriver, selenideProxy);
  }
}
