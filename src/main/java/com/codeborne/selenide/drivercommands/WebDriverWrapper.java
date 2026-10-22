package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.WebDriverInstance;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.codeborne.selenide.impl.Plugins.inject;

/**
 * A `Driver` implementation which uses given webdriver [and proxy].
 * It doesn't open a new browser.
 * It doesn't start a new proxy.
 */
public class WebDriverWrapper implements Driver {
  private static final Logger log = LoggerFactory.getLogger(WebDriverWrapper.class);

  private final WebDriverInstance wd;
  private final ScreenShotLaboratory screenshots;
  private final BrowserHealthChecker browserHealthChecker = new BrowserHealthChecker();

  public WebDriverWrapper(Config config, WebDriver webDriver,
                          @Nullable SelenideProxyServer selenideProxy, DownloadsFolder browserDownloadsFolder) {
    this(config, webDriver, selenideProxy, browserDownloadsFolder, inject());
  }

  public WebDriverWrapper(Config config, WebDriver webDriver,
                          @Nullable SelenideProxyServer selenideProxy, DownloadsFolder browserDownloadsFolder,
                          ScreenShotLaboratory screenshots) {
    this.wd = new WebDriverInstance(config, webDriver, selenideProxy, browserDownloadsFolder);
    this.screenshots = screenshots;
  }

  @Override
  public Config config() {
    return wd.config();
  }

  @Override
  public ScreenShotLaboratory screenshots() {
    return screenshots;
  }

  @Override
  public Browser browser() {
    return new Browser(wd.config().browser(), wd.config().headless());
  }

  @Override
  public boolean hasWebDriverStarted() {
    return wd.webDriver() != null;
  }

  @Override
  public WebDriver getWebDriver() {
    return wd.webDriver();
  }

  @Override
  public SelenideProxyServer getProxy() {
    return wd.proxy();
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    if (wd.webDriver() == null || !browserHealthChecker.isBrowserStillOpen(wd.webDriver())) {
      log.info("Webdriver has been closed meanwhile");
      close();
      throw new IllegalStateException("Webdriver has been closed meanwhile");
    }
    return wd.webDriver();
  }

  @Override
  @Nullable
  public DownloadsFolder browserDownloadsFolder() {
    return wd.downloadsFolder();
  }

  @Override
  public List<LogEntry> getBrowserLogs() {
    return wd.browserLogs();
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
    wd.dispose();
  }
}
