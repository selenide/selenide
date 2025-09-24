package com.codeborne.selenide.appium;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapperTest.add;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapperTest.getUrl;
import static org.openqa.selenium.remote.CapabilityType.BROWSER_NAME;

class FakeWebDriver extends RemoteWebDriver {
  FakeWebDriver(MutableCapabilities capabilities, Browser browser) {
    super(getUrl(), add(capabilities, BROWSER_NAME, browser.browserName()));
  }

  @Override
  protected void startSession(Capabilities capabilities) {
  }
}
