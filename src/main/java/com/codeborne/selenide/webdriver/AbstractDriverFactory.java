package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverProvider;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.browserVersion;
import static com.codeborne.selenide.Configuration.pageLoadStrategy;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.openqa.selenium.remote.CapabilityType.*;

abstract class AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(ChromeDriverFactory.class.getName());

  abstract boolean supports();

  abstract WebDriver create(Proxy proxy);

  static WebDriver createDefault(final Proxy proxy) {
    return createInstanceOf(browser, proxy);
  }

  static WebDriver createInstanceOf(final String className, final Proxy proxy) {
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

  protected static RuntimeException runtime(Throwable exception) {
    return exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception);
  }

  static DesiredCapabilities createCommonCapabilities(final Proxy proxy) {
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

  private static DesiredCapabilities transferCapabilitiesFromSystemProperties(DesiredCapabilities currentBrowserCapabilities, String prefix) {
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

  static DesiredCapabilities createFirefoxCapabilities(Proxy proxy) {
    FirefoxProfile myProfile = new FirefoxProfile();
    myProfile.setPreference("network.automatic-ntlm-auth.trusted-uris", "http://,https://");
    myProfile.setPreference("network.automatic-ntlm-auth.allow-non-fqdn", true);
    myProfile.setPreference("network.negotiate-auth.delegation-uris", "http://,https://");
    myProfile.setPreference("network.negotiate-auth.trusted-uris", "http://,https://");
    myProfile.setPreference("network.http.phishy-userpass-length", 255);
    myProfile.setPreference("security.csp.enable", false);

    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    myProfile = transferFirefoxProfileFromSystemProperties(myProfile, "firefoxprofile.");
    capabilities.setCapability("marionette", false);
    capabilities.setCapability(FirefoxDriver.PROFILE, myProfile);
    return capabilities;
  }

  private static FirefoxProfile transferFirefoxProfileFromSystemProperties(FirefoxProfile currentFirefoxProfile, String prefix) {
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
}
