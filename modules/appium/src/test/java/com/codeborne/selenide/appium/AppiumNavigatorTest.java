package com.codeborne.selenide.appium;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeborne.selenide.Configuration;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.gecko.GeckoDriver;
import io.appium.java_client.ios.IOSDriver;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AppiumNavigatorTest {
  private final AppiumNavigator appiumNavigator = new AppiumNavigator();

  @BeforeEach
  @AfterEach
  void resetSettings() {
    Configuration.browserSize = "1024x768";
    Configuration.pageLoadTimeout = 30_000;
  }

  @Test
  void shouldSetupConfiguration() {
    appiumNavigator.launchApp(() -> () -> {
    });
    assertThat(Configuration.browserSize).isNull();
    assertThat(Configuration.pageLoadTimeout).isEqualTo(0);
  }

  @Test
  void terminateApp_shouldSendAppIdForAndroid() {
    AndroidDriver androidDriver = mock(AndroidDriver.class);
    ArgumentCaptor<Map<String, String>> params = ArgumentCaptor.forClass(Map.class);
    ArgumentCaptor<String> script = ArgumentCaptor.forClass(String.class);
    when(androidDriver.executeScript(script.capture(), params.capture())).thenReturn("ok");

    appiumNavigator.terminateApp(androidDriver, "com.github.foo");

    assertThat(script.getValue()).isEqualTo("mobile:terminateApp");
    assertThat(params.getValue()).hasSize(1);
    assertThat(params.getValue().get("appId")).isEqualTo("com.github.foo");
  }

  @Test
  void terminateApp_shouldSendBundleIdForIOS() {
    IOSDriver androidDriver = mock(IOSDriver.class);
    ArgumentCaptor<Map<String, String>> params = ArgumentCaptor.forClass(Map.class);
    ArgumentCaptor<String> script = ArgumentCaptor.forClass(String.class);
    when(androidDriver.executeScript(script.capture(), params.capture())).thenReturn("ok");

    appiumNavigator.terminateApp(androidDriver, "com.github.foo");

    assertThat(script.getValue()).isEqualTo("mobile:terminateApp");
    assertThat(params.getValue()).hasSize(1);
    assertThat(params.getValue().get("bundleId")).isEqualTo("com.github.foo");
  }

  @Test
  void shouldFailForUnsupportedDrivers() {
    GeckoDriver geckoDriver = mock(GeckoDriver.class);
    assertThatThrownBy(() -> appiumNavigator.terminateApp(geckoDriver, "com.github.foo"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Cannot terminate application for unknown driver class ")
      .hasMessageContaining("GeckoDriver");
  }
}
