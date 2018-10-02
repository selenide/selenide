package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static io.github.bonigarcia.wdm.WebDriverManager.config;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class WebDriverBinaryManager {
  private static final Logger log = Logger.getLogger(WebDriverBinaryManager.class.getName());

  public void setupBinaryPath(Browser browser) {
    if (browser.isChrome()) setupChrome();
    if (browser.isEdge()) setupEdge();
    if (browser.isIE()) setupIE();
    if (browser.isOpera()) setupOpera();
    if (browser.isPhantomjs()) setupPhantomjs();
    if (browser.isFirefox()) setupFirefox();
  }

  private void setupChrome() {
    cacheMeIfYouCan("webdriver.chrome.driver", () -> {
      WebDriverManager.chromedriver().setup();
    });
  }

  private void setupEdge() {
    cacheMeIfYouCan("webdriver.edge.driver", () -> {
      WebDriverManager.edgedriver().setup();
    });
  }

  private void setupIE() {
    cacheMeIfYouCan("webdriver.ie.driver", () -> {
      WebDriverManager.iedriver().setup();
    });
  }

  private void setupOpera() {
    cacheMeIfYouCan("webdriver.opera.driver", () -> {
      WebDriverManager.operadriver().setup();
    });
  }

  private void setupPhantomjs() {
    cacheMeIfYouCan("phantomjs.binary.path", () -> {
      WebDriverManager.phantomjs().setup();
    });
  }

  private void setupFirefox() {
    cacheMeIfYouCan("webdriver.gecko.driver", () -> {
      WebDriverManager.firefoxdriver().setup();
    });
  }

  private void cacheMeIfYouCan(String systemPropertyName, Runnable webdriverSetup) {
    if (webdriverIsAlreadyInitialized(systemPropertyName)) {
      log.info("Skip: webdriver is already initialized: " + System.getProperty(systemPropertyName));
      return;
    }

    File lastCheckIndicator = new File(config().getTargetPath(), lastModifiedFileName(systemPropertyName));
    boolean canUseCachedWebdriver = lastCheckIndicator.exists() && hasRecentlyCheckedForUpdates(lastCheckIndicator);
    log.info("lastCheckIndicator=" + lastCheckIndicator.getAbsolutePath() +
      ", exists=" + lastCheckIndicator.exists() + ", lastModified=" + new Date(lastCheckIndicator.lastModified()) +
      ", now=" + new Date() + ", diff: " + (System.currentTimeMillis() - lastCheckIndicator.lastModified()) + " ms.");

    if (canUseCachedWebdriver) {
      log.info("Can use cache");
      config().setForceCache(true);
    }
    else {
      log.info("Cannot use cache");
    }

    webdriverSetup.run();

    if (!canUseCachedWebdriver) {
      long ts = System.currentTimeMillis();
      log.info("Mark as recently checked: " + lastCheckIndicator.getAbsolutePath() + ", ts=" + ts + ", now=" + new Date(ts));
      markAsRecentlyChecked(lastCheckIndicator);
    }
    else {
      long ts = System.currentTimeMillis();
      log.info("Not marking as recently checked: " + lastCheckIndicator.getAbsolutePath() + ", ts=" + ts + ", now=" + new Date(ts));
    }
  }

  private boolean webdriverIsAlreadyInitialized(String systemPropertyName) {
    return isNotBlank(System.getProperty(systemPropertyName, ""));
  }

  private String lastModifiedFileName(String systemPropertyName) {
    return systemPropertyName + '.' + config().getOs().toLowerCase() + config().getArchitecture() + ".timestamp";
  }

  private boolean hasRecentlyCheckedForUpdates(File lastCheckIndicator) {
    return System.currentTimeMillis() - lastCheckIndicator.lastModified() < TimeUnit.HOURS.toMillis(4);
  }

  private void markAsRecentlyChecked(File lastCheckIndicator) {
    if (!lastCheckIndicator.exists()) {
      try {
        if (!lastCheckIndicator.createNewFile()) {
          throw new RuntimeException("Failed to create " + lastCheckIndicator.getAbsolutePath());
        }
      }
      catch (IOException e) {
        throw new RuntimeException("Failed to create " + lastCheckIndicator.getAbsolutePath(), e);
      }
    }
    else if (!lastCheckIndicator.setLastModified(System.currentTimeMillis())) {
      throw new RuntimeException("Failed to touch " + lastCheckIndicator.getAbsolutePath());
    }
  }
}
