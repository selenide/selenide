package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.WebDriverInstance;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A `Driver` implementation which uses given webdriver [and proxy].
 * It doesn't open a new browser.
 * It doesn't start a new proxy.
 */
public class WebDriverWrapper implements Driver {
  private static final Logger log = LoggerFactory.getLogger(WebDriverWrapper.class);

  private final WebDriverInstance wd;
  private final BrowserHealthChecker browserHealthChecker = new BrowserHealthChecker();

  public WebDriverWrapper(Config config, WebDriver webDriver,
                          @Nullable SelenideProxyServer selenideProxy, DownloadsFolder browserDownloadsFolder) {
    this(new WebDriverInstance(config, webDriver, selenideProxy, browserDownloadsFolder));
  }

  private WebDriverWrapper(WebDriverInstance wd) {
    this.wd = wd;
  }

  @Override
  public Config config() {
    return wd.config();
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
