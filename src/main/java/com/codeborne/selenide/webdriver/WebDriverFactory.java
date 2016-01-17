package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverProvider;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.openqa.selenium.remote.CapabilityType.*;

public class WebDriverFactory {
  private static final Logger log = Logger.getLogger(WebDriverFactory.class.getName());

  public WebDriver createWebDriver(Proxy proxy) {
    log.config("Configuration.browser=" + browser);
    log.config("Configuration.remote=" + remote);
    log.config("Configuration.startMaximized=" + startMaximized);

    WebDriver webdriver = remote != null ? createRemoteDriver(remote, browser, proxy) :
        CHROME.equalsIgnoreCase(browser) ? createChromeDriver(proxy) :
            isFirefox() ? createFirefoxDriver(proxy) :
                isHtmlUnit() ? createHtmlUnitDriver(proxy) :
                    isIE() ? createInternetExplorerDriver(proxy) :
                        isPhantomjs() ? createPhantomJsDriver(proxy) :
                            isOpera() ? createOperaDriver(proxy) :
                                isSafari() ? createSafariDriver(proxy) :
                                    createInstanceOf(browser, proxy);
    webdriver = maximize(webdriver);
    if (!isHeadless()) {
      Capabilities capabilities = ((RemoteWebDriver) webdriver).getCapabilities();
      log.info("BrowserName=" + capabilities.getBrowserName() + " Version=" + capabilities.getVersion()
              + " Platfrom=" + capabilities.getPlatform());
    }
    return webdriver;
  }

  protected WebDriver createRemoteDriver(String remote, String browser, Proxy proxy) {
    try {
      DesiredCapabilities capabilities = createCommonCapabilities(proxy);
      capabilities.setBrowserName(browser);
      return new RemoteWebDriver(new URL(remote), capabilities);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + remote, e);
    }
  }


  protected DesiredCapabilities createCommonCapabilities(Proxy proxy) {
    DesiredCapabilities browserCapabilities = new DesiredCapabilities();
    if (proxy != null) {
      browserCapabilities.setCapability(PROXY, proxy);
    }
    return browserCapabilities;
  }

  protected WebDriver createChromeDriver(Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    ChromeOptions options = new ChromeOptions();
    options.addArguments("test-type");
    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
    return new ChromeDriver(capabilities);
  }

  protected WebDriver createFirefoxDriver(Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    return new FirefoxDriver(capabilities);
  }

  protected WebDriver createHtmlUnitDriver(Proxy proxy) {
    DesiredCapabilities capabilities = DesiredCapabilities.htmlUnitWithJs();
    capabilities.merge(createCommonCapabilities(proxy));
    capabilities.setCapability(HtmlUnitDriver.INVALIDSELECTIONERROR, true);
    capabilities.setCapability(HtmlUnitDriver.INVALIDXPATHERROR, false);
    if (browser.indexOf(':') > -1) {
      // Use constants BrowserType.IE, BrowserType.FIREFOX, BrowserType.CHROME etc.
      String emulatedBrowser = browser.replaceFirst("htmlunit:(.*)", "$1");
      capabilities.setVersion(emulatedBrowser);
    }
    return new HtmlUnitDriver(capabilities);
  }

  protected WebDriver createInternetExplorerDriver(Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    return new InternetExplorerDriver(capabilities);
  }

  protected WebDriver createPhantomJsDriver(Proxy proxy) {
    return createInstanceOf("org.openqa.selenium.phantomjs.PhantomJSDriver", proxy);
  }

  protected WebDriver createOperaDriver(Proxy proxy) {
    return createInstanceOf("com.opera.core.systems.OperaDriver", proxy);
  }

  protected WebDriver createSafariDriver(Proxy proxy) {
    return createInstanceOf("org.openqa.selenium.safari.SafariDriver", proxy);
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

    org.openqa.selenium.Dimension screenResolution = new org.openqa.selenium.Dimension(
        (int) toolkit.getScreenSize().getWidth(),
        (int) toolkit.getScreenSize().getHeight());

    window.setSize(screenResolution);
    window.setPosition(new org.openqa.selenium.Point(0, 0));
  }

  protected WebDriver createInstanceOf(String className, Proxy proxy) {
    try {
      DesiredCapabilities capabilities = createCommonCapabilities(proxy);
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
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return ((WebDriverProvider) constructor.newInstance()).createDriver(capabilities);
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
}
