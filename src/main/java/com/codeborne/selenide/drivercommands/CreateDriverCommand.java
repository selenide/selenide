package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.BrowserDownloadsFolder;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.FileNamer;
import com.codeborne.selenide.impl.Plugins;
import com.codeborne.selenide.impl.WebDriverInstance;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.proxy.SelenideProxyServerFactory;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.net.HostIdentifier;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;

import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static com.codeborne.selenide.logevents.SelenideLogger.getReadableSubject;
import static java.lang.Thread.currentThread;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class CreateDriverCommand {
  private static final Logger log = LoggerFactory.getLogger(CreateDriverCommand.class);
  private final FileNamer fileNamer;

  public CreateDriverCommand() {
    this(new FileNamer());
  }

  CreateDriverCommand(FileNamer fileNamer) {
    this.fileNamer = fileNamer;
  }

  @Nonnull
  public WebDriverInstance createDriver(Config config,
                                        WebDriverFactory factory,
                                        @Nullable Proxy userProvidedProxy,
                                        List<WebDriverEventListener> eventListeners,
                                        List<WebDriverListener> listeners) {
    return SelenideLogger.get("webdriver", getReadableSubject("create"), () -> {
      log.debug("Creating webdriver in thread {} (ip: {}, host: {})...",
        currentThread().getId(), HostIdentifier.getHostAddress(), HostIdentifier.getHostName());

      SelenideProxyServer selenideProxyServer = null;
      Proxy browserProxy = userProvidedProxy;

      if (config.proxyEnabled()) {
        SelenideProxyServerFactory selenideProxyServerFactory = Plugins.inject(SelenideProxyServerFactory.class);
        try {
          selenideProxyServer = selenideProxyServerFactory.create(config, userProvidedProxy);
          browserProxy = selenideProxyServer.getSeleniumProxy();
        }
        catch (NoClassDefFoundError e) {
          throw new IllegalStateException("Cannot initialize proxy. " +
            "Probably you should add \"selenide-proxy\" dependency to your project " +
            "- see https://central.sonatype.com/search?q=selenide-proxy&namespace=com.codeborne", e);
        }
      }

      File browserDownloadsFolder = config.remote() != null ? null :
        ensureFolderExists(new File(config.downloadsFolder(), fileNamer.generateFileName()).getAbsoluteFile());
      BrowserDownloadsFolder downloadsFolder = BrowserDownloadsFolder.from(browserDownloadsFolder);

      WebDriver webdriver = factory.createWebDriver(config, browserProxy, browserDownloadsFolder);

      log.info("Created webdriver in thread {}: {} -> {}",
        currentThread().getId(), webdriver.getClass().getSimpleName(), webdriver);

      WebDriver webDriver = addListeners(webdriver, eventListeners, listeners);
      WebDriverInstance result = new WebDriverInstance(config, webDriver, selenideProxyServer, downloadsFolder);
      WebdriversRegistry.register(result);
      return result;
    });
  }

  @Nonnull
  private WebDriver addListeners(WebDriver webdriver,
                                 List<WebDriverEventListener> eventListeners,
                                 List<WebDriverListener> listeners) {
    return addWebDriverListeners(
      addEventListeners(webdriver, eventListeners),
      listeners
    );
  }

  @Nonnull
  private WebDriver addEventListeners(WebDriver webdriver, List<WebDriverEventListener> eventListeners) {
    if (eventListeners.isEmpty()) {
      return webdriver;
    }
    EventFiringWebDriver wrapper = new EventFiringWebDriver(webdriver);
    for (WebDriverEventListener listener : eventListeners) {
      log.info("Add listener to webdriver: {}", listener);
      wrapper.register(listener);
    }
    return wrapper;
  }

  @Nonnull
  private WebDriver addWebDriverListeners(WebDriver webdriver, List<WebDriverListener> listeners) {
    if (listeners.isEmpty()) {
      return webdriver;
    }

    log.info("Add listeners to webdriver: {}", listeners);
    EventFiringDecorator<WebDriver> wrapper = new EventFiringDecorator<>(listeners.toArray(new WebDriverListener[]{}));
    return wrapper.decorate(webdriver);
  }
}
