package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

public interface DriverFactory {
  WebDriver create(Config config, Proxy proxy);
}
