package com.codeborne.selenide.appium;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.appium.java_client.android.AndroidDriver;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DeepLinkLauncherTest {
  DeepLinkLauncher deepLinkLauncher = new DeepLinkLauncher();

  @Test
  void shouldSendAppIdForAndroid() {
    AndroidDriver androidDriver = mock(AndroidDriver.class);
    ArgumentCaptor<Map<String, String>> params = ArgumentCaptor.forClass(Map.class);
    ArgumentCaptor<String> script = ArgumentCaptor.forClass(String.class);
    when(androidDriver.executeScript(script.capture(), params.capture())).thenReturn("ok");

    deepLinkLauncher.openDeepLinkOnAndroid(androidDriver, "mydemoapprn://product-details/1", "com.saucelabs.mydemoapp.rn");

    assertThat(script.getValue()).isEqualTo("mobile:deepLink");
    assertThat(params.getValue()).hasSize(2);
    assertThat(params.getValue().get("url")).isEqualTo("mydemoapprn://product-details/1");
    assertThat(params.getValue().get("package")).isEqualTo("com.saucelabs.mydemoapp.rn");
  }
}
