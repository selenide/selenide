package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Proxy;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class WebDriverBinaryManager {

  private Proxy proxy;

  public void setupBinaryPath(Browser browser, Proxy proxy) {
    this.proxy = proxy;
    if (browser.isChrome()) setupChrome();
    if (browser.isEdge()) setupEdge();
    if (browser.isIE()) setupIE();
    if (browser.isOpera()) setupOpera();
    if (browser.isPhantomjs()) setupPhantomjs();
    if (browser.isFirefox()) setupFirefox();
  }

  private void setupChrome() {
    if (isSystemPropertySet("webdriver.chrome.driver")) {
      WebDriverManager webDriverManager = WebDriverManager.chromedriver();
      if (this.proxy != null) webDriverManager.proxy(proxy.getHttpProxy());
      webDriverManager.setup();
    }
  }

  private void setupEdge() {
    if (isSystemPropertySet("webdriver.edge.driver")) {
      WebDriverManager webDriverManager = WebDriverManager.edgedriver();
      if (this.proxy != null) webDriverManager.proxy(proxy.getHttpProxy());
      webDriverManager.setup();
    }
  }

  private void setupIE() {
    if (isSystemPropertySet("webdriver.ie.driver")) {
      WebDriverManager webDriverManager = WebDriverManager.iedriver();
      if (this.proxy != null) webDriverManager.proxy(proxy.getHttpProxy());
    }
  }

  private void setupOpera() {
    if (isSystemPropertySet("webdriver.opera.driver")) {
      WebDriverManager webDriverManager = WebDriverManager.operadriver();
      if (this.proxy != null) webDriverManager.proxy(proxy.getHttpProxy());
    }
  }

  private void setupPhantomjs() {
    if (isSystemPropertySet("phantomjs.binary.path")) {
      WebDriverManager webDriverManager = WebDriverManager.phantomjs();
      if (this.proxy != null) webDriverManager.proxy(proxy.getHttpProxy());
    }
  }

  private void setupFirefox() {
    if (isSystemPropertySet("webdriver.gecko.driver")) {
      WebDriverManager webDriverManager = WebDriverManager.firefoxdriver();
      if (this.proxy != null) webDriverManager.proxy(proxy.getHttpProxy());
    }
  }

  private boolean isSystemPropertySet(String key) {
    return isBlank(System.getProperty(key, ""));
  }
}
