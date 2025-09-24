package com.codeborne.selenide.appium;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.Capabilities;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapperTest.getUrl;

class FakeAndroidDriver extends AndroidDriver {
  FakeAndroidDriver(Capabilities capabilities) {
    super(getUrl(), new UiAutomator2Options(capabilities));
  }

  @Override
  protected void startSession(Capabilities capabilities) {
  }
}
