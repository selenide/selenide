package com.codeborne.selenide.appium;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.WebPageSourceExtractor;
import org.openqa.selenium.WebDriver;

import java.io.File;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;

public class AppiumScreenSourceExtractor extends WebPageSourceExtractor {
  @Override
  protected File createFile(Config config, WebDriver webDriver, String fileName) {
    return isMobile(webDriver) ?
      new File(config.reportsFolder(), fileName + ".xml") :
      super.createFile(config, webDriver, fileName);
  }
}
