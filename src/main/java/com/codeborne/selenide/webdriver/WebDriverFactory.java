package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverProvider;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.impl.Describe.describe;
import static org.openqa.selenium.remote.CapabilityType.*;

public class WebDriverFactory {
  private static final Logger log = Logger.getLogger(WebDriverFactory.class.getName());

  public WebDriver createWebDriver(Proxy proxy) {
    log.config("Configuration.browser=" + browser);
    log.config("Configuration.browser.version=" + browserVersion);
    log.config("Configuration.remote=" + remote);
    log.config("Configuration.browserSize=" + browserSize);
    log.config("Configuration.startMaximized=" + startMaximized);

    WebDriverBinaryManager.setupBinaryPath();

    WebDriver webdriver = remote != null ? createRemoteDriver(remote, browser, proxy) :
            CHROME.equalsIgnoreCase(browser) ? createChromeDriver(proxy) :
                    isMarionette() ? createMarionetteDriver(proxy) :
                            isFirefox() ? createFirefoxDriver(proxy) :
                                    isHtmlUnit() ? createHtmlUnitDriver(proxy) :
                                            isEdge() ? createEdgeDriver(proxy) :
                                                    isIE() ? createInternetExplorerDriver(proxy) :
                                                            isPhantomjs() ? createPhantomJsDriver(proxy) :
                                                                    isOpera() ? createOperaDriver(proxy) :
                                                                            isSafari() ? createSafariDriver(proxy) :
                                                                                    isJBrowser() ? createJBrowserDriver(proxy) :
                                                                                            createInstanceOf(browser, proxy);
    webdriver = adjustBrowserSize(webdriver);
    if (!isHeadless()) {
      Capabilities capabilities = ((RemoteWebDriver) webdriver).getCapabilities();
      log.info("BrowserName=" + capabilities.getBrowserName() + " Version=" + capabilities.getVersion()
              + " Platform=" + capabilities.getPlatform());
    }
    log.info("Selenide v. " + Selenide.class.getPackage().getImplementationVersion());
    if (remote == null) {
      BuildInfo seleniumInfo = new BuildInfo();
      log.info("Selenium WebDriver v. " + seleniumInfo.getReleaseLabel() + " build time: " + seleniumInfo.getBuildTime());
    } else {
      ((RemoteWebDriver) webdriver).setFileDetector(new LocalFileDetector());
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
    if (browserVersion != null && !browserVersion.isEmpty()) {
      browserCapabilities.setVersion(browserVersion);
    }
    browserCapabilities.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, pageLoadStrategy);
    browserCapabilities.setCapability("acceptSslCerts", true);

    browserCapabilities = transferCapabilitiesFromSystemProperties(browserCapabilities, "capabilities.");
    return browserCapabilities;
  }

  private DesiredCapabilities transferCapabilitiesFromSystemProperties(DesiredCapabilities currentBrowserCapabilities, String prefix) {
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        log.config("Use " + key + "=" + value);
        if (value.equals("true") || value.equals("false")) {
          currentBrowserCapabilities.setCapability(capability, Boolean.valueOf(value));
        } else if (value.matches("^-?\\d+$")) { //if integer
          currentBrowserCapabilities.setCapability(capability, Integer.parseInt(value));
        } else {
          currentBrowserCapabilities.setCapability(capability, value);
        }
      }
    }
    return currentBrowserCapabilities;
  }

  private FirefoxProfile transferFirefoxProfileFromSystemProperties(FirefoxProfile currentFirefoxProfile, String prefix) {
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        log.config("Use " + key + "=" + value);
        if (value.equals("true") || value.equals("false")) {
          currentFirefoxProfile.setPreference(capability, Boolean.valueOf(value));
        } else if (value.matches("^-?\\d+$")) { //if integer
          currentFirefoxProfile.setPreference(capability, Integer.parseInt(value));
        } else {
          currentFirefoxProfile.setPreference(capability, value);
        }
      }
    }
    return currentFirefoxProfile;
  }

  /**
   * This method only handles so-called "arguments" for ChromeOptions (there is also "ExperimentalOptions", "Extensions" etc.)
   *
   * @param currentChromeOptions
   * @param prefix
   * @return
   */
  private ChromeOptions transferChromeOptionsFromSystemProperties(ChromeOptions currentChromeOptions, String prefix) {
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        if (capability.equals("args")) {
          List<String> args = Arrays.asList(value.split(","));
          currentChromeOptions.addArguments(args);
        } else {
          log.warning(capability + "is ignored." +
                  "Only so-called arguments (chromeoptions.args=<values comma separated>) " +
                  "are supported for the chromeoptions at the moment");
        }
      }
    }
    return currentChromeOptions;
  }


  protected WebDriver createChromeDriver(Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    ChromeOptions options = createChromeOptions();
    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
    return new ChromeDriver(capabilities);
  }

  protected ChromeOptions createChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--no-sandbox");  // This make Chromium reachable (?)
    if (chromeSwitches != null) {
      options.addArguments(chromeSwitches);
    }
    options = transferChromeOptionsFromSystemProperties(options, "chromeoptions.");
    try {
      log.config("Chrome options:" + options.toJson().toString());
    } catch (IOException e) {
      log.warning("Error while reading from file:" + e.getMessage() + ". Ignoring it.");
      e.printStackTrace(System.err);
    }
    return options;
  }

  protected WebDriver createFirefoxDriver(Proxy proxy) {
    DesiredCapabilities capabilities = createFirefoxCapabilities(proxy);
    log.info("Firefox 48+ is currently not supported by Selenium Firefox driver. " +
            "Use browser=marionette with geckodriver, when using it.");
    return new FirefoxDriver(capabilities);
  }

  protected DesiredCapabilities createFirefoxCapabilities(Proxy proxy) {
    FirefoxProfile myProfile = new FirefoxProfile();
    myProfile.setPreference("network.automatic-ntlm-auth.trusted-uris", "http://,https://");
    myProfile.setPreference("network.automatic-ntlm-auth.allow-non-fqdn", true);
    myProfile.setPreference("network.negotiate-auth.delegation-uris", "http://,https://");
    myProfile.setPreference("network.negotiate-auth.trusted-uris", "http://,https://");
    myProfile.setPreference("network.http.phishy-userpass-length", 255);
    myProfile.setPreference("security.csp.enable", false);

    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    myProfile = transferFirefoxProfileFromSystemProperties(myProfile, "firefoxprofile.");
    capabilities.setCapability(FirefoxDriver.PROFILE, myProfile);
    capabilities.setCapability("marionette", false);
    return capabilities;
  }

  protected WebDriver createMarionetteDriver(Proxy proxy) {
    DesiredCapabilities capabilities = createFirefoxCapabilities(proxy);
    capabilities.setCapability("marionette", true);
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

  protected WebDriver createEdgeDriver(Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    return new EdgeDriver(capabilities);
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

  protected WebDriver createJBrowserDriver(Proxy proxy) {
    return createInstanceOf("com.machinepublishers.jbrowserdriver.JBrowserDriver", proxy);
  }

  protected WebDriver adjustBrowserSize(WebDriver driver) {
    if (browserSize != null) {
      log.info("Set browser size to " + browserSize);
      String[] dimension = browserSize.split("x");
      int width = Integer.parseInt(dimension[0]);
      int height = Integer.parseInt(dimension[1]);
      driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
    } else if (startMaximized) {
      try {
        if (isChrome()) {
          maximizeChromeBrowser(driver.manage().window());
        } else {
          driver.manage().window().maximize();
        }
      } catch (Exception cannotMaximize) {
        log.warning("Cannot maximize " + describe(driver) + ": " + cannotMaximize);
      }
    }
    return driver;
  }

  protected void maximizeChromeBrowser(WebDriver.Window window) {
    // Chrome driver does not yet support maximizing. Let' apply black magic!
    org.openqa.selenium.Dimension screenResolution = getScreenSize();

    window.setSize(screenResolution);
    window.setPosition(new org.openqa.selenium.Point(0, 0));
  }

  Dimension getScreenSize() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();

    return new Dimension(
            (int) toolkit.getScreenSize().getWidth(),
            (int) toolkit.getScreenSize().getHeight());
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
                new String[]{"--web-security=no", "--ignore-ssl-errors=yes"});
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
    } catch (InvocationTargetException e) {
      throw runtime(e.getTargetException());
    } catch (Exception invalidClassName) {
      throw new IllegalArgumentException(invalidClassName);
    }
  }

  protected RuntimeException runtime(Throwable exception) {
    return exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception);
  }
}
