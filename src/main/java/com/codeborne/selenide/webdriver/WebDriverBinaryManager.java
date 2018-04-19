package com.codeborne.selenide.webdriver;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.EdgeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;
import io.github.bonigarcia.wdm.OperaDriverManager;
import io.github.bonigarcia.wdm.PhantomJsDriverManager;

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
      ChromeDriverManager.getInstance().setup();
    } else if (isEdge() && !isSystemPropertySet("webdriver.edge.driver")) {
      EdgeDriverManager.getInstance().setup();
    } else if (isIE() && !isSystemPropertySet("webdriver.ie.driver")) {
      InternetExplorerDriverManager.getInstance().setup();
    } else if (isOpera() && !isSystemPropertySet("webdriver.opera.driver")) {
      OperaDriverManager.getInstance().setup();
    } else if (isPhantomjs() && !isSystemPropertySet("phantomjs.binary.path")) {
      PhantomJsDriverManager.getInstance().setup();
    } else if (isFirefox() && !isSystemPropertySet("webdriver.gecko.driver")) {
      FirefoxDriverManager.getInstance().setup();
    }
  }

  private boolean isSystemPropertySet(String key) {
    return isNotBlank(System.getProperty(key, ""));
  }
}
