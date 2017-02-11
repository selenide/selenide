package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.*;
import org.apache.commons.lang3.StringUtils;

import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.*;

/**
 * Created by sergey on 11.02.17.
 */
public class WebDriverBinaryManager {
  private static final Logger log = Logger.getLogger(WebDriverFactory.class.getName());

  private static final String EDGE_PROPERTY = "webdriver.edge.driver";
  private static final String CHROME_PROPERTY = "webdriver.chrome.driver";
  private static final String IE_PROPERTY = "webdriver.ie.driver";
  private static final String OPERA_PROPERTY = "webdriver.opera.driver";
  private static final String PHANTOM_PROPERTY = "phantomjs.binary.path";
  private static final String MARIONETTE_PROPERTY = "webdriver.gecko.driver";

  static void setupBinaryPath() {
    try {
      if (isChrome() &&
          isEmptyProperty(CHROME_PROPERTY)) {
        ChromeDriverManager.getInstance().setup();
      } else if (isEdge() &&
          isEmptyProperty(EDGE_PROPERTY)) {
        EdgeDriverManager.getInstance().setup();
      } else if (isIE() && isEmptyProperty(IE_PROPERTY)) {
        InternetExplorerDriverManager.getInstance().setup();
      } else if (isOpera() &&
          isEmptyProperty(OPERA_PROPERTY)) {
        OperaDriverManager.getInstance().setup();
      } else if (isPhantomjs() &&
          isEmptyProperty(PHANTOM_PROPERTY)) {
        PhantomJsDriverManager.getInstance().setup();
      } else if (isMarionette() &&
          isEmptyProperty(MARIONETTE_PROPERTY)) {
        FirefoxDriverManager.getInstance().setup();
      }
    } catch (final Exception ex) {
      log.warning("Problem to load driver binary for " + Configuration.browser);
    }
  }

  private static boolean isEmptyProperty(String property) {
    return StringUtils.isBlank(System.getProperty(property));
  }
}
