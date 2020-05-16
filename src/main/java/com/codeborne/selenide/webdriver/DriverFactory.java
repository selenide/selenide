package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

public interface DriverFactory {
  void setupWebdriverBinary();
  MutableCapabilities createCapabilities(Config config, Browser browser, Proxy proxy);
  WebDriver create(Config config, Browser browser, Proxy proxy);
}
