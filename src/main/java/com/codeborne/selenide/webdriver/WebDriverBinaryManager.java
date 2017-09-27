package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.*;
import org.apache.commons.lang3.StringUtils;

import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.*;

/**
 * Created by sepi on 15.02.17.
 */
public class WebDriverBinaryManager {

  private static final Logger log = Logger.getLogger(WebDriverBinaryManager.class.getName());

  private static final String CHROME_PROP = "webdriver.chrome.driver";
  private static final String MARIONETTE_PROP = "webdriver.gecko.driver";
  private static final String OPERA_PROP = "webdriver.opera.driver";
  private static final String EDGE_PROP = "webdriver.edge.driver";
  private static final String IE_PROP = "webdriver.ie.driver";
  private static final String PHANTOM_PROP = "phantomjs.binary.path";

  static void setupBinaryPath() {
    try {
      if (isChrome() && isEmptyProperty(CHROME_PROP)) {
        ChromeDriverManager.getInstance().setup();
      } else if (isEdge() && isEmptyProperty(EDGE_PROP)) {
        EdgeDriverManager.getInstance().setup();
      } else if (isIE() && isEmptyProperty(IE_PROP)) {
        InternetExplorerDriverManager.getInstance().setup();
      } else if (isOpera() && isEmptyProperty(OPERA_PROP)) {
        OperaDriverManager.getInstance().setup();
      } else if (isPhantomjs() && isEmptyProperty(PHANTOM_PROP)) {
        PhantomJsDriverManager.getInstance().setup();
      } else if (isMarionette() && isEmptyProperty(MARIONETTE_PROP)) {
        FirefoxDriverManager.getInstance().setup();
      } else {
        log.warning(Configuration.browser + " doesn't require binary driver");
      }
    } catch (final Exception ex) {
      log.warning("Problem to load driver binary for " + Configuration.browser + ": " + ex);
    }
  }

  private static boolean isEmptyProperty(String key) {
    return StringUtils.isBlank(System.getProperty(key, ""));
  }

}
