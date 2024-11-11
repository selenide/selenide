package it.mobile;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.codeborne.selenide.junit5.TextReportExtension;
import com.codeborne.selenide.webdriver.HttpClientTimeouts;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.appmanagement.ApplicationState;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.appium.SelenideAppium.launchApp;
import static io.appium.java_client.service.local.flags.GeneralServerFlag.RELAXED_SECURITY;
import static it.mobile.BrowserstackUtils.isCi;
import static java.time.Duration.ofMinutes;

@ExtendWith({TextReportExtension.class, ScreenShooterExtension.class, BrowserstackExtension.class})
public abstract class ITTest {
  private static final Logger log = LoggerFactory.getLogger(ITTest.class);
  @Nullable
  private static volatile AppiumDriverLocalService service;

  @BeforeAll
  static void initAppium() {
    if (isCi()) {
      return;
    }
    if (service == null) {
      startAppium();
    }
  }

  private static synchronized void startAppium() {
    if (service == null) {
      var appium = new AppiumServiceBuilder()
        .withArgument(RELAXED_SECURITY)
        .withIPAddress("127.0.0.1")
        .withTimeout(ofMinutes(3))
        .build();
      appium.start();
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        log.info("Stopping Appium..");
        appium.stop();
        log.info("Appium stopped.");
      }));
      service = appium;
    }
  }

  @BeforeEach
  final void resetSettings() {
    Configuration.timeout = 10_000;
    Configuration.pageLoadTimeout = -1;
    Configuration.remoteConnectionTimeout = Duration.ofSeconds(10).toMillis();
    Configuration.remoteReadTimeout = ofMinutes(5).toMillis();
    HttpClientTimeouts.defaultLocalReadTimeout = ofMinutes(4);
    HttpClientTimeouts.defaultLocalConnectTimeout = ofMinutes(3);
  }

  protected void prepareApp(InteractsWithApps mobileDriver, String bundleId) {
    ApplicationState applicationState = mobileDriver.queryAppState(bundleId);
    switch (applicationState) {
      case NOT_INSTALLED -> {
        closeWebDriver();
        launchApp();
      }
      case NOT_RUNNING, RUNNING_IN_BACKGROUND, RUNNING_IN_BACKGROUND_SUSPENDED, RUNNING_IN_FOREGROUND -> {
        mobileDriver.terminateApp(bundleId);
        Selenide.executeJavaScript("mobile: clearApp", Map.of("appId", bundleId));
        mobileDriver.activateApp(bundleId);
        // deep link opening may not work after activating the app. we have to wait ¯\_(ツ)_/¯
        Selenide.sleep(1_000);
      }
    }
  }
}
