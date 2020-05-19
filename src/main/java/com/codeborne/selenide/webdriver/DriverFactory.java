package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

public interface DriverFactory {
  void setupWebdriverBinary();
  WebDriver create(Config config, Browser browser, Proxy proxy);
}
