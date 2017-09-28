package com.codeborne.selenide.webdriver;

import io.github.bonigarcia.wdm.*;

import static com.codeborne.selenide.WebDriverRunner.*;
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
    } else if (isMarionette() && !isSystemPropertySet("webdriver.gecko.driver")) {
      FirefoxDriverManager.getInstance().setup();
    }
  }

  private boolean isSystemPropertySet(String key) {
    return isNotBlank(System.getProperty(key, ""));
  }
}
