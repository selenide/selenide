package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public interface DriverFactory {
  void setupWebdriverBinary();

  @CheckReturnValue
  @Nonnull
  MutableCapabilities createCapabilities(Config config, Browser browser, @Nullable Proxy proxy, File browserDownloadsFolder);

  @CheckReturnValue
  @Nonnull
  WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, File browserDownloadsFolder);
}
