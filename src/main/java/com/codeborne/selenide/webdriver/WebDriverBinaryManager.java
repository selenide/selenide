package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.*;

import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.*;

/**
 * Created by sergey on 11.02.17.
 */
public class WebDriverBinaryManager {
  private static final Logger log = Logger.getLogger(WebDriverFactory.class.getName());

  static void setupBinaryPath() {
    try {
      if (isChrome()) {
        ChromeDriverManager.getInstance().setup();
      } else if (isEdge()) {
        EdgeDriverManager.getInstance().setup();
      } else if (isIE()) {
        InternetExplorerDriverManager.getInstance().setup();
      } else if (isOpera()) {
        OperaDriverManager.getInstance().setup();
      } else if (isPhantomjs()) {
        PhantomJsDriverManager.getInstance().setup();
      } else if (isMarionette()) {
        FirefoxDriverManager.getInstance().setup();
      } else {
        log.warning(Configuration.browser + "doesn't require binary driver");
      }
    } catch (final Exception ex) {
      log.warning("Problem to load driver binary for " + Configuration.browser);
    }
  }
}
