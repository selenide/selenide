package com.codeborne.selenide.impl;

import com.codeborne.selenide.WebDriverProvider;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static java.lang.Thread.currentThread;
import static org.openqa.selenium.remote.CapabilityType.*;

public class WebDriverThreadLocalContainer {
  protected List<WebDriverEventListener> listeners = new ArrayList<WebDriverEventListener>();
  protected Collection<Thread> ALL_WEB_DRIVERS_THREADS = new ConcurrentLinkedQueue<Thread>();
  protected Map<Long, WebDriver> THREAD_WEB_DRIVER = new ConcurrentHashMap<Long, WebDriver>(4);

  protected final AtomicBoolean cleanupThreadStarted = new AtomicBoolean(false);

  protected void closeUnusedWebdrivers() {
    for (Thread thread : ALL_WEB_DRIVERS_THREADS) {
      if (!thread.isAlive()) {
        closeWebDriver(thread);
      }
    }
  }

  public void addListener(WebDriverEventListener listener) {
    listeners.add(listener);
  }

  public WebDriver setWebDriver(WebDriver webDriver) {
    THREAD_WEB_DRIVER.put(currentThread().getId(), webDriver);
    return webDriver;
  }

  protected boolean isBrowserStillOpen(WebDriver webDriver) {
    try {
      webDriver.getTitle();
      return true;
    } catch (UnreachableBrowserException e) {
      return false;
    } catch (NoSuchWindowException e) {
      return false;
    } catch (SessionNotFoundException e) {
      return false;
    }
  }

  public WebDriver getWebDriver() {
    WebDriver webDriver = THREAD_WEB_DRIVER.get(currentThread().getId());
    return webDriver != null ? webDriver : setWebDriver(createDriver());
  }

  public WebDriver getAndCheckWebDriver() {
    WebDriver webDriver = THREAD_WEB_DRIVER.get(currentThread().getId());
    if (webDriver != null) {
      if (isBrowserStillOpen(webDriver)) {
        return webDriver;
      }
      else {
        System.out.println("Webdriver has been closed meanwhile. Let's re-create it.");
        closeWebDriver();
      }
    }
    return setWebDriver(createDriver());

  }

  public void closeWebDriver() {
    closeWebDriver(currentThread());
  }

  protected void closeWebDriver(Thread thread) {
    ALL_WEB_DRIVERS_THREADS.remove(thread);
    WebDriver webdriver = THREAD_WEB_DRIVER.remove(thread.getId());

    if (webdriver != null && !holdBrowserOpen) {
      System.out.println(" === CLOSE WEBDRIVER: " + thread.getId() + " -> " + webdriver);

      try {
        webdriver.quit();
      }
      catch (UnreachableBrowserException ignored) {
        // It happens for Firefox. It's ok: browser is already closed.
      }
      catch (WebDriverException cannotCloseBrowser) {
        System.err.println("Cannot close browser normally: " + Cleanup.of.webdriverExceptionMessage(cannotCloseBrowser));
      }
      finally {
        killBrowser(webdriver);
      }
    }
  }

  protected void killBrowser(WebDriver webdriver) {
    if (webdriver instanceof Killable) {
      try {
        ((Killable) webdriver).kill();
      } catch (Exception e) {
        System.err.println("Failed to kill browser " + webdriver + ':');
        e.printStackTrace();
      }
    }
  }

  public void clearBrowserCache() {
    WebDriver webdriver = THREAD_WEB_DRIVER.get(currentThread().getId());
    if (webdriver != null) {
      webdriver.manage().deleteAllCookies();
    }
  }

  public String getPageSource() {
    return getWebDriver().getPageSource();
  }

  public String getCurrentUrl() {
    return getWebDriver().getCurrentUrl();
  }

  protected WebDriver createDriver() {
    WebDriver webdriver = remote != null ? createRemoteDriver(remote, browser) :
        CHROME.equalsIgnoreCase(browser) ? createChromeDriver() :
            isFirefox() ? createFirefoxDriver() :
                isHtmlUnit() ? createHtmlUnitDriver() :
                    isIE() ? createInternetExplorerDriver() :
                        isPhantomjs() ? createPhantomJsDriver() :
                            isOpera() ? createOperaDriver() :
                                isSafari() ? createSafariDriver() :
                                  createInstanceOf(browser);
    webdriver = maximize(webdriver);

    System.out.println(" === CREATE WEBDRIVER: " + currentThread().getId() + " -> " + webdriver);

    return markForAutoClose(listeners.isEmpty() ? webdriver : addListeners(webdriver));
  }

  protected WebDriver addListeners(WebDriver webdriver) {
    EventFiringWebDriver wrapper = new EventFiringWebDriver(webdriver);
    for (WebDriverEventListener listener : listeners) {
      wrapper.register(listener);
    }
    return wrapper;
  }

  protected WebDriver markForAutoClose(WebDriver webDriver) {
    ALL_WEB_DRIVERS_THREADS.add(currentThread());

    if (!cleanupThreadStarted.get()) {
      synchronized (cleanupThreadStarted) {
        if (!cleanupThreadStarted.get()) {
          new UnusedWebdriversCleanupThread().start();
          cleanupThreadStarted.set(true);
        }
      }
    }
    Runtime.getRuntime().addShutdownHook(new WebdriversFinalCleanupThread(currentThread()));
    return webDriver;
  }

  protected WebDriver createChromeDriver() {
    return new ChromeDriver();
  }

  protected WebDriver createFirefoxDriver() {
    return new FirefoxDriver();
  }

  protected WebDriver createHtmlUnitDriver() {
    DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
    capabilities.setCapability(HtmlUnitDriver.INVALIDSELECTIONERROR, true);
    capabilities.setCapability(HtmlUnitDriver.INVALIDXPATHERROR, false);
    capabilities.setJavascriptEnabled(true);
    if (browser.indexOf(':') > -1) {
      // Use constants BrowserType.IE, BrowserType.FIREFOX, BrowserType.CHROME etc.
      String emulatedBrowser = browser.replaceFirst("htmlunit:(.*)", "$1");
      capabilities.setVersion(emulatedBrowser);
    }
    return new HtmlUnitDriver(capabilities);
  }

  protected WebDriver createInternetExplorerDriver() {
    return new InternetExplorerDriver();
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
        System.out.println("Cannot maximize " + browser + ": " + cannotMaximize);
      }
    }
    return driver;
  }

  protected void maximizeChromeBrowser(WebDriver.Window window) {
    // Chrome driver does not yet support maximizing. Let' apply black magic!
    java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();

    Dimension screenResolution = new Dimension(
        (int) toolkit.getScreenSize().getWidth(),
        (int) toolkit.getScreenSize().getHeight());

    window.setSize(screenResolution);
    window.setPosition(new org.openqa.selenium.Point(0, 0));
  }

  protected WebDriver createInstanceOf(String className) {
    try {
      DesiredCapabilities capabilities = new DesiredCapabilities();
      capabilities.setJavascriptEnabled(true);
      capabilities.setCapability(TAKES_SCREENSHOT, true);
      capabilities.setCapability(ACCEPT_SSL_CERTS, true);
      capabilities.setCapability(SUPPORTS_ALERTS, true);

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
      DesiredCapabilities capabilities = new DesiredCapabilities();
      capabilities.setBrowserName(browser);
      return new RemoteWebDriver(new URL(remote), capabilities);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + remote, e);
    }
  }

  protected class WebdriversFinalCleanupThread extends Thread {
    private final Thread thread;

    public WebdriversFinalCleanupThread(Thread thread) {
      this.thread = thread;
    }

    @Override
    public void run() {
      closeWebDriver(thread);
    }
  }

  protected class UnusedWebdriversCleanupThread extends Thread {
    public UnusedWebdriversCleanupThread() {
      setDaemon(true);
      setName("Webdrivers killer thread");
    }

    @Override
    public void run() {
      while (true) {
        closeUnusedWebdrivers();
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }
  }
}
