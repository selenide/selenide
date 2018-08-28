package com.codeborne.selenide.webdriver;

import io.github.bonigarcia.wdm.WebDriverManager;

import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.isOpera;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class WebDriverBinaryManager {
  public void setupBinaryPath() {
    if (isChrome()) setupChrome();
    if (isEdge()) setupEdge();
    if (isIE()) setupIE();
    if (isOpera()) setupOpera();
    if (isPhantomjs()) setupPhantomjs();
    if (isFirefox()) setupFirefox();
  }

  private void setupChrome() {
    if (!isSystemPropertySet("webdriver.chrome.driver")) {
      WebDriverManager.chromedriver().setup();
    }
  }

  private void setupEdge() {
    if (!isSystemPropertySet("webdriver.edge.driver")) {
      WebDriverManager.edgedriver().setup();
    }
  }

  private void setupIE() {
    if (!isSystemPropertySet("webdriver.ie.driver")) {
      WebDriverManager.iedriver().setup();
    }
  }

  private void setupOpera() {
    if (!isSystemPropertySet("webdriver.opera.driver")) {
      WebDriverManager.operadriver().setup();
    }
  }

  private void setupPhantomjs() {
    if (!isSystemPropertySet("phantomjs.binary.path")) {
      WebDriverManager.phantomjs().setup();
    }
  }

  private void setupFirefox() {
    if (!isSystemPropertySet("webdriver.gecko.driver")) {
      WebDriverManager.firefoxdriver().setup();
    }
  }

  private boolean isSystemPropertySet(String key) {
    return isNotBlank(System.getProperty(key, ""));
  }
}
