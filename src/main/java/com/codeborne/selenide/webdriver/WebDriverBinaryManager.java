package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import io.github.bonigarcia.wdm.WebDriverManager;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class WebDriverBinaryManager {
  public void setupBinaryPath(Browser browser) {
    if (browser.isChrome()) setupChrome();
    if (browser.isEdge()) setupEdge();
    if (browser.isIE()) setupIE();
    if (browser.isOpera()) setupOpera();
    if (browser.isPhantomjs()) setupPhantomjs();
    if (browser.isFirefox()) setupFirefox();
  }

  private void setupChrome() {
    if (isSystemPropertySet("webdriver.chrome.driver")) {
      WebDriverManager.chromedriver().setup();
    }
  }

  private void setupEdge() {
    if (isSystemPropertySet("webdriver.edge.driver")) {
      WebDriverManager.edgedriver().setup();
    }
  }

  private void setupIE() {
    if (isSystemPropertySet("webdriver.ie.driver")) {
      WebDriverManager.iedriver().setup();
    }
  }

  private void setupOpera() {
    if (isSystemPropertySet("webdriver.opera.driver")) {
      WebDriverManager.operadriver().setup();
    }
  }

  private void setupPhantomjs() {
    if (isSystemPropertySet("phantomjs.binary.path")) {
      WebDriverManager.phantomjs().setup();
    }
  }

  private void setupFirefox() {
    if (isSystemPropertySet("webdriver.gecko.driver")) {
      WebDriverManager.firefoxdriver().setup();
    }
  }

  private boolean isSystemPropertySet(String key) {
    return isBlank(System.getProperty(key, ""));
  }
}
