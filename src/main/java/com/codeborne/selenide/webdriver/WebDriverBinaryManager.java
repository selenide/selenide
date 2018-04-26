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
    if (isChrome() && !isSystemPropertySet("webdriver.chrome.driver")) {
      WebDriverManager.chromedriver().setup();
    } else if (isEdge() && !isSystemPropertySet("webdriver.edge.driver")) {
      WebDriverManager.edgedriver().setup();
    } else if (isIE() && !isSystemPropertySet("webdriver.ie.driver")) {
      WebDriverManager.iedriver().setup();
    } else if (isOpera() && !isSystemPropertySet("webdriver.opera.driver")) {
      WebDriverManager.operadriver().setup();
    } else if (isPhantomjs() && !isSystemPropertySet("phantomjs.binary.path")) {
      WebDriverManager.phantomjs().setup();
    } else if (isFirefox() && !isSystemPropertySet("webdriver.gecko.driver")) {
      WebDriverManager.firefoxdriver().setup();
    }
  }

  private boolean isSystemPropertySet(String key) {
    return isNotBlank(System.getProperty(key, ""));
  }
}
