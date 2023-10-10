package it.mobile;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.TextReportExtension;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.appium.java_client.service.local.flags.GeneralServerFlag.RELAXED_SECURITY;
import static java.time.Duration.ofSeconds;

@ExtendWith(TextReportExtension.class)
public abstract class ITTest {
  private static AppiumDriverLocalService service;

  @BeforeAll
  static void startAppium() {
    AppiumServiceBuilder appiumServiceBuilder = new AppiumServiceBuilder()
      .withArgument(RELAXED_SECURITY)
      .withTimeout(ofSeconds(30));
    service = AppiumDriverLocalService.buildService(appiumServiceBuilder);
    service.start();
  }

  @AfterAll
  static void stopAppium() {
    service.stop();
  }

  @BeforeEach
  final void resetSettings() {
    Configuration.timeout = 10_000;
    Configuration.pageLoadTimeout = -1;
  }
}
