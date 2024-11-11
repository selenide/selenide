package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.io.File;

public interface DriverFactory {
  MutableCapabilities createCapabilities(Config config, Browser browser,
                                         @Nullable Proxy proxy, @Nullable File browserDownloadsFolder);

  WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder);
}
