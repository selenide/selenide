package com.codeborne.selenide.appium;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.Capabilities;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapperTest.getUrl;

class FakeIOSDriver extends IOSDriver {
  FakeIOSDriver(Capabilities capabilities) {
    super(getUrl(), new XCUITestOptions(capabilities));
  }

  @Override
  protected void startSession(Capabilities capabilities) {
  }
}
