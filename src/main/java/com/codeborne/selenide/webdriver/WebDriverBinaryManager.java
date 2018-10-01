package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.github.bonigarcia.wdm.WebDriverManager.config;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
      return;
    }

    File lastCheckIndicator = new File(config().getTargetPath(), lastModifiedFileName(systemPropertyName));
    boolean canUseCachedWebdriver = lastCheckIndicator.exists() && hasRecentlyCheckedForUpdates(lastCheckIndicator);
    if (canUseCachedWebdriver) {
      config().setForceCache(true);
    }

    webdriverSetup.run();

    if (!canUseCachedWebdriver) {
      markAsRecentlyChecked(lastCheckIndicator);
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
