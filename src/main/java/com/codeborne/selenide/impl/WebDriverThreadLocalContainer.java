package com.codeborne.selenide.impl;

import com.codeborne.selenide.WebDriverProvider;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.internal.Killable;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.lang.reflect.Constructor;
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

  protected final AtomicBoolean killerThreadRun = new AtomicBoolean(false);

  protected void closeUnusedWebdrivers() {
    for (Thread thread : ALL_WEB_DRIVERS_THREADS) {
      if (!thread.isAlive()) {
        closeWebDriver(thread);
      }
    }
  }

  protected void closeAllWebDrivers() {
    for (Thread thread : ALL_WEB_DRIVERS_THREADS) {
      closeWebDriver(thread);
    }
  }

  public void addListener(WebDriverEventListener listener) {
    listeners.add(listener);
  }

  public WebDriver setWebDriver(WebDriver webDriver) {
    THREAD_WEB_DRIVER.put(currentThread().getId(), webDriver);
    return webDriver;
  }

  protected WebDriver autoClose(WebDriver webDriver) {
    System.out.println(" === CREATE WEBDRIVER: " + currentThread().getId() + " -> " + webDriver);
    ALL_WEB_DRIVERS_THREADS.add(currentThread());

    if (!killerThreadRun.get()) {
      synchronized (killerThreadRun) {
        if (!killerThreadRun.get()) {
          new KillerThread().start();
          Runtime.getRuntime().addShutdownHook(new AbsoluteKillerThread());
          killerThreadRun.set(true);
        }
      }
    }
    return webDriver;
  }

  public WebDriver getWebDriver() {
    if (!THREAD_WEB_DRIVER.containsKey(currentThread().getId())) {
      return setWebDriver(autoClose(createDriver()));
    }
    // TODO check if browser is already closed
    return THREAD_WEB_DRIVER.get(currentThread().getId());
  }

  public void closeWebDriver() {
    closeWebDriver(currentThread());
  }

  protected void closeWebDriver(Thread thread) {
    ALL_WEB_DRIVERS_THREADS.remove(thread);
    WebDriver webdriver = THREAD_WEB_DRIVER.remove(thread.getId());

    System.out.println(" === CLOSE WEBDRIVER: " + currentThread().getId() + " -> " + webdriver);

    if (webdriver != null && !holdBrowserOpen) {
      try {
        webdriver.quit();
      } catch (WebDriverException cannotCloseBrowser) {
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
                            OPERA.equalsIgnoreCase(browser) ? createOperaDriver() :
                                isSafari() ? createSafariDriver() :
                                  createInstanceOf(browser);
    return listeners.isEmpty() ? webdriver : addListeners(webdriver);
  }

  protected WebDriver addListeners(WebDriver webdriver) {
    EventFiringWebDriver wrapper = new EventFiringWebDriver(webdriver);
    for (WebDriverEventListener listener : listeners) {
      wrapper.register(listener);
    }
    return wrapper;
  }

  protected WebDriver createChromeDriver() {
    ChromeOptions options = new ChromeOptions();
    if (startMaximized) {
      // Due do bug in ChromeDriver we need this workaround
      // http://stackoverflow.com/questions/3189430/how-do-i-maximize-the-browser-window-using-webdriver-selenium-2
      options.addArguments("chrome.switches", "--start-maximized");
    }
    return new ChromeDriver(options);
  }

  protected WebDriver createFirefoxDriver() {
    return maximize(new FirefoxDriver());
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
    return maximize(new InternetExplorerDriver());
  }

  protected WebDriver createPhantomJsDriver() {
    return maximize(createInstanceOf("org.openqa.selenium.phantomjs.PhantomJSDriver"));
  }

  protected WebDriver createOperaDriver() {
    return createInstanceOf("com.opera.core.systems.OperaDriver");
  }

  protected WebDriver createSafariDriver() {
    return createInstanceOf("org.openqa.selenium.safari.SafariDriver");
  }

  protected WebDriver maximize(WebDriver driver) {
    if (startMaximized) {
      driver.manage().window().maximize();
    }
    return driver;
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
    catch (Exception invalidClassName) {
      throw new IllegalArgumentException(invalidClassName);
    }
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

  protected class AbsoluteKillerThread extends Thread {
      @Override
      public void run() {
        closeAllWebDrivers();
      }
  }

  protected class KillerThread extends Thread {
    public KillerThread() {
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
