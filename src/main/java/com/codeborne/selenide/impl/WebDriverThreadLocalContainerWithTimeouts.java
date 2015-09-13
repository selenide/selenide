package com.codeborne.selenide.impl;

import com.codeborne.selenide.WebDriverProvider;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.internal.Killable;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import static org.openqa.selenium.remote.CapabilityType.*;

public class WebDriverThreadLocalContainerWithTimeouts implements WebDriverContainer {
  private static final Logger log = Logger.getLogger(WebDriverThreadLocalContainerWithTimeouts.class.getName());

  protected final List<WebDriverEventListener> listeners = new ArrayList<>();
  protected final List<WebDriver> CREATED_WEB_DRIVERS = new CopyOnWriteArrayList<>();
  protected final List<ThreadWebdriver> THREAD_WEB_DRIVERS = new CopyOnWriteArrayList<>();
  protected final Map<Long, WebDriver> THREAD_WEB_DRIVER = new ConcurrentHashMap<>();
  protected Proxy webProxySettings;

  protected final AtomicBoolean systemShutdownStarted = new AtomicBoolean(false);
  protected final AtomicBoolean cleanupThreadStarted = new AtomicBoolean(false);
  protected Thread unusedWebdriversCleanupThread = new UnusedWebdriversCleanupThread();
  protected WebdriversFinalCleanupThread finalCleanupThread = new WebdriversFinalCleanupThread();

  protected void closeWebdriversCreatedByDeadThreads() {
    for (ThreadWebdriver tw : THREAD_WEB_DRIVERS) {
      if (!tw.thread.isAlive()) {
        log.info("Thread " + tw.thread.getId() + " is dead. Let's close webdriver " + tw.webdriver);
        THREAD_WEB_DRIVERS.remove(tw);
        closeWebDriver(tw.webdriver);
      }
    }
  }

  protected void closeWebdriversNotBoundToAnyThread() {
    for (WebDriver webdriver : CREATED_WEB_DRIVERS) {
      if (!THREAD_WEB_DRIVER.containsValue(webdriver)) {
        log.info("Webdriver is not used by any thread. Let's close it: " + webdriver);
        CREATED_WEB_DRIVERS.remove(webdriver);
        closeWebDriver(webdriver);
      }
    }
  }

  @Override
  public void addListener(WebDriverEventListener listener) {
    listeners.add(listener);
  }

  @Override
  public WebDriver setWebDriver(WebDriver webDriver) {
    THREAD_WEB_DRIVER.put(currentThread().getId(), webDriver);
    return webDriver;
  }
  
  @Override
  public void setProxy(Proxy webProxy) {
    webProxySettings = webProxy;
  }

  protected boolean isBrowserStillOpen(WebDriver webDriver) {
    try {
      webDriver.getTitle();
      return true;
    } catch (UnreachableBrowserException e) {
      log.log(FINE, "Browser is unreachable", e);
      return false;
    } catch (NoSuchWindowException e) {
      log.log(FINE, "Browser window is now found", e);
      return false;
    } catch (SessionNotFoundException e) {
      log.log(FINE, "Browser session is not found", e);
      return false;
    }
  }

  /**
   * @return true iff webdriver is started in current thread 
   */
  @Override
  public boolean hasWebDriverStarted() {
    return THREAD_WEB_DRIVER.containsKey(currentThread().getId());
  }
  
  @Override
  public WebDriver getWebDriver() {
    WebDriver webDriver = THREAD_WEB_DRIVER.get(currentThread().getId());
    if (webDriver != null) {
      return webDriver;
    }

    log.info("No webdriver is bound to current thread: " + currentThread().getId() + " - let's create new webdriver");
    return setWebDriver(createDriver());
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    WebDriver webDriver = THREAD_WEB_DRIVER.get(currentThread().getId());
    if (webDriver != null) {
      if (isBrowserStillOpen(webDriver)) {
        return webDriver;
      }
      else {
        log.info("Webdriver has been closed meanwhile. Let's re-create it.");
        closeWebDriver();
      }
    }
    return setWebDriver(createDriver());
  }

  @Override
  public void closeWebDriver() {
    closeAllWebDriversOf(currentThread());
  }

  protected void closeAllWebDriversOf(Thread thread) {
    for (ThreadWebdriver tw : THREAD_WEB_DRIVERS) {
      if (tw.thread.equals(thread)) {
        log.info("Let's close webdriver created by thread " + tw.thread.getId() + ": " + tw.webdriver);
        THREAD_WEB_DRIVERS.remove(tw);
        closeWebDriver(tw.webdriver);
      }
    }
  }

  protected void closeWebDriver(WebDriver webdriver) {
    if (webdriver != null && !holdBrowserOpen) {
      CREATED_WEB_DRIVERS.remove(webdriver);

      for (Map.Entry<Long, WebDriver> entry : THREAD_WEB_DRIVER.entrySet()) {
        if (entry.getValue().equals(webdriver))
          THREAD_WEB_DRIVER.remove(entry.getKey());
        log.info("Close webdriver: " + entry.getKey() + " -> " + webdriver);
      }
      
      long start = currentTimeMillis();

      Thread t = new Thread(new CloseBrowser(webdriver));
      t.setDaemon(true);
      t.start();
      
      try {
        t.join(closeBrowserTimeoutMs);
      } catch (InterruptedException e) {
        log.log(FINE, "Failed to close webdriver in " + closeBrowserTimeoutMs + " milliseconds", e);
      }

      long duration = currentTimeMillis() - start;
      if (duration >= closeBrowserTimeoutMs) {
        log.severe("Failed to close webdriver in " + closeBrowserTimeoutMs + " milliseconds");
      }
      else if (duration > 200) {
        log.info("Closed webdriver in " + duration + " ms");
      }
      else {
        log.fine("Closed webdriver in " + duration + " ms");
      }
    }
  }

  private class CloseBrowser implements Runnable {
    private final WebDriver webdriver;

    private CloseBrowser(WebDriver webdriver) {
      this.webdriver = webdriver;
    }

    @Override
    public void run() {
      log.info("Trying to close the browser " + webdriver + " ...");

      try {
        webdriver.close();
      }
      catch (UnreachableBrowserException e) {
        // It happens for Firefox. It's ok: browser is already closed.
        log.log(FINE, "Cannot close, browser is unreachable", e);
      }

      try {
        webdriver.quit();
      }
      catch (UnreachableBrowserException e) {
        // It happens for Firefox. It's ok: browser is already closed.
        log.log(FINE, "Cannot quit, browser is unreachable", e);
      }
      catch (WebDriverException cannotCloseBrowser) {
        log.severe("Cannot close browser normally: " + Cleanup.of.webdriverExceptionMessage(cannotCloseBrowser));
      }
      finally {
        killBrowser(webdriver);
        CREATED_WEB_DRIVERS.remove(webdriver);
      }
    }

    protected void killBrowser(WebDriver webdriver) {
      if (webdriver instanceof Killable) {
        try {
          ((Killable) webdriver).kill();
        } catch (Exception e) {
          log.log(SEVERE, "Failed to kill browser " + webdriver + ':', e);
        }
      }
    }
  }
  
  @Override
  public void clearBrowserCache() {
    WebDriver webdriver = THREAD_WEB_DRIVER.get(currentThread().getId());
    if (webdriver != null) {
      webdriver.manage().deleteAllCookies();
    }
  }

  @Override
  public String getPageSource() {
    return getWebDriver().getPageSource();
  }

  @Override
  public String getCurrentUrl() {
    return getWebDriver().getCurrentUrl();
  }

  protected WebDriver createDriver() {
    WebDriver webdriver = createWebDriverWithTimeout();
    webdriver = maximize(webdriver);
    log.info("Create webdriver in current thread " + currentThread().getId() + ": " + browser + " -> " + webdriver);
    markForAutoClose(currentThread(), addListeners(webdriver));
    return webdriver;
  }

  protected WebDriver createWebDriverWithTimeout() {
    for (int i = 0; i < 3; i++) {
      CreateWebdriver create = new CreateWebdriver();
      Thread t = new Thread(create);
      t.setName(t.getName() + ": create webdriver for " + currentThread());
      t.setDaemon(true);
      t.start();
      try {
        long start = currentTimeMillis();
        while (create.webdriver == null && currentTimeMillis() - start <= openBrowserTimeoutMs) {
          Thread.sleep(100);
        }
        if (create.webdriver != null) {
          return create.webdriver;
        }
      }
      catch (InterruptedException e) {
        throw runtime(e);
      }
    }
    throw new RuntimeException("Could not create webdriver in " + openBrowserTimeoutMs + " ms");
  }

  private class CreateWebdriver implements Runnable {
    WebDriver webdriver = null;

    @Override
    public void run() {
      try {
        if (systemShutdownStarted.get()) {
          log.warning("System shutdown started. Cancel webdriver creation.");
          return;
        }
        
        log.config("Configuration.browser=" + browser);
        log.config("Configuration.remote=" + remote);
        log.config("Configuration.startMaximized=" + startMaximized);

        webdriver = remote != null ? createRemoteDriver(remote, browser) :
          CHROME.equalsIgnoreCase(browser) ? createChromeDriver() :
              isFirefox() ? createFirefoxDriver() :
                  isHtmlUnit() ? createHtmlUnitDriver() :
                      isIE() ? createInternetExplorerDriver() :
                          isPhantomjs() ? createPhantomJsDriver() :
                              isOpera() ? createOperaDriver() :
                                  isSafari() ? createSafariDriver() :
                                      createInstanceOf(browser);

        Thread.sleep(200); // TODO avoid sleeping in working hours
        addShutdownHook(new Thread(new CloseBrowser(webdriver)));
        CREATED_WEB_DRIVERS.add(webdriver);
      }
      catch (Exception e) {
        log.log(SEVERE, "Failed to create webdriver", e);
      }
    }
  }

  protected void addShutdownHook(Thread hook) {
    Runtime.getRuntime().addShutdownHook(hook);
  }

  protected WebDriver addListeners(WebDriver webdriver) {
    if (listeners.isEmpty()) {
      return webdriver;
    }
    
    EventFiringWebDriver wrapper = new EventFiringWebDriver(webdriver);
    for (WebDriverEventListener listener : listeners) {
      log.info("Add listener to webdriver: " + listener);
      wrapper.register(listener);
    }
    return wrapper;
  }

  protected void markForAutoClose(Thread thread, WebDriver webDriver) {
    THREAD_WEB_DRIVERS.add(new ThreadWebdriver(thread, webDriver));

    if (!cleanupThreadStarted.get()) {
      synchronized (this) {
        if (!cleanupThreadStarted.get()) {
          unusedWebdriversCleanupThread.start();
          addShutdownHook(finalCleanupThread);
          cleanupThreadStarted.set(true);
        }
      }
    }
  }

  protected WebDriver createChromeDriver() {
    DesiredCapabilities capabilities = createCommonCapabilities();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("test-type");
    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
    return new ChromeDriver(capabilities);
  }

  protected WebDriver createFirefoxDriver() {
    DesiredCapabilities capabilities = createCommonCapabilities();
    return new FirefoxDriver(capabilities);
  }

  protected WebDriver createHtmlUnitDriver() {
    DesiredCapabilities capabilities = DesiredCapabilities.htmlUnitWithJs();
    capabilities.merge(createCommonCapabilities());
    capabilities.setCapability(HtmlUnitDriver.INVALIDSELECTIONERROR, true);
    capabilities.setCapability(HtmlUnitDriver.INVALIDXPATHERROR, false);
    if (browser.indexOf(':') > -1) {
      // Use constants BrowserType.IE, BrowserType.FIREFOX, BrowserType.CHROME etc.
      String emulatedBrowser = browser.replaceFirst("htmlunit:(.*)", "$1");
      capabilities.setVersion(emulatedBrowser);
    }
    return new HtmlUnitDriver(capabilities);
  }

  protected WebDriver createInternetExplorerDriver() {
    DesiredCapabilities capabilities = createCommonCapabilities();
    return new InternetExplorerDriver(capabilities);
  }

  protected WebDriver createPhantomJsDriver() {
    return createInstanceOf("org.openqa.selenium.phantomjs.PhantomJSDriver");
  }

  protected WebDriver createOperaDriver() {
    return createInstanceOf("com.opera.core.systems.OperaDriver");
  }

  protected WebDriver createSafariDriver() {
    return createInstanceOf("org.openqa.selenium.safari.SafariDriver");
  }

  protected WebDriver maximize(WebDriver driver) {
    if (startMaximized) {
      try {
        if (isChrome()) {
          maximizeChromeBrowser(driver.manage().window());
        }
        else {
          driver.manage().window().maximize();
        }
      }
      catch (Exception cannotMaximize) {
        log.warning("Cannot maximize " + browser + ": " + cannotMaximize);
      }
    }
    return driver;
  }

  protected void maximizeChromeBrowser(WebDriver.Window window) {
    // Chrome driver does not yet support maximizing. Let' apply black magic!
    Toolkit toolkit = Toolkit.getDefaultToolkit();

    Dimension screenResolution = new Dimension(
        (int) toolkit.getScreenSize().getWidth(),
        (int) toolkit.getScreenSize().getHeight());

    window.setSize(screenResolution);
    window.setPosition(new Point(0, 0));
  }

  protected WebDriver createInstanceOf(String className) {
    try {
      DesiredCapabilities capabilities = createCommonCapabilities();
      capabilities.setJavascriptEnabled(true);
      capabilities.setCapability(TAKES_SCREENSHOT, true);
      capabilities.setCapability(ACCEPT_SSL_CERTS, true);
      capabilities.setCapability(SUPPORTS_ALERTS, true);
      if (isPhantomjs()) {
        capabilities.setCapability("phantomjs.cli.args", // PhantomJSDriverService.PHANTOMJS_CLI_ARGS == "phantomjs.cli.args" 
            new String[] {"--web-security=no", "--ignore-ssl-errors=yes"});
      }

      Class<?> clazz = Class.forName(className);
      if (WebDriverProvider.class.isAssignableFrom(clazz)) {
        return ((WebDriverProvider) clazz.newInstance()).createDriver(capabilities);
      } else {
        Constructor<?> constructor = Class.forName(className).getConstructor(Capabilities.class);
        return (WebDriver) constructor.newInstance(capabilities);
      }
    }
    catch (InvocationTargetException e) {
      throw runtime(e.getTargetException());
    }
    catch (Exception invalidClassName) {
      throw new IllegalArgumentException(invalidClassName);
    }
  }

  protected RuntimeException runtime(Throwable exception) {
    return exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception);
  }

  protected WebDriver createRemoteDriver(String remote, String browser) {
    try {
      DesiredCapabilities capabilities = createCommonCapabilities();
      capabilities.setBrowserName(browser);
      return new RemoteWebDriver(new URL(remote), capabilities);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + remote, e);
    }
  }

  protected DesiredCapabilities createCommonCapabilities() {
    DesiredCapabilities browserCapabilities = new DesiredCapabilities();
    if (webProxySettings != null) {
      browserCapabilities.setCapability(PROXY, webProxySettings);
    }
    return browserCapabilities;
  }
  
  protected class WebdriversFinalCleanupThread extends Thread {
    @Override
    public void run() {
      systemShutdownStarted.set(true);
      log.info("System shutdown. Let's close " + CREATED_WEB_DRIVERS.size() + " webdrivers.");
      unusedWebdriversCleanupThread.interrupt();
      
      while (!CREATED_WEB_DRIVERS.isEmpty()) {
        try {
          log.info("System shutdown. Waiting for " + CREATED_WEB_DRIVERS.size() + " webdrivers still open.");
          sleep(100);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      log.info("System shutdown. Closed all webdrivers.");
    }
  }

  protected class UnusedWebdriversCleanupThread extends Thread {
    public UnusedWebdriversCleanupThread() {
      setDaemon(true);
      setName("Webdrivers killer thread");
    }

    @Override
    public void run() {
      while (!systemShutdownStarted.get()) {
        closeWebdriversCreatedByDeadThreads();
        closeWebdriversNotBoundToAnyThread();
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }
  }

  private static class ThreadWebdriver {
    final Thread thread;
    final WebDriver webdriver;


    private ThreadWebdriver(Thread thread, WebDriver webdriver) {
      this.thread = thread;
      this.webdriver = webdriver;
    }
  }
}
