package com.codeborne.selenide;

import org.openqa.selenium.remote.DesiredCapabilities;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.WebDriverRunner.FIREFOX;

public interface SelenideConfig {

   default long timeout(){
     return Long.parseLong(System.getProperty("selenide.timeout", "4000"));
   }

   default long pollingInterval(){
     return Long.parseLong(System.getProperty("selenide.pollingInterval", "100"));
   }

   default long collectionsPollingInterval(){
     return Long.parseLong(System.getProperty("selenide.collectionsPollingInterval", "200"));
   }


  default String baseUrl(){
     return System.getProperty("selenide.baseUrl", "http://localhost:8080");
  }

  default long collectionsTimeout(){
    return Long.parseLong(System.getProperty("selenide.collectionsTimeout", "6000"));
  }

  default boolean holdBrowserOpen(){
    return Boolean.getBoolean("selenide.holdBrowserOpen");
  }

  default boolean reopenBrowserOnFail(){
    return Boolean.parseBoolean(
      System.getProperty("selenide.reopenBrowserOnFail", "true"));
  }

  default long openBrowserTimeoutMs(){
    return Long.parseLong(System.getProperty("selenide.openBrowserTimeout", "15000"));
  }

  default long closeBrowserTimeoutMs(){
    return Long.parseLong(System.getProperty("selenide.closeBrowserTimeout", "5000"));
  }


  default String browser(){
    return System.getProperty("selenide.browser", System.getProperty("browser", FIREFOX));
  }

  default String browserVersion(){
   return System.getProperty("selenide.browserVersion",
     System.getProperty("selenide.browser.version", System.getProperty("browser.version")));
  }

  default String remote(){
    return System.getProperty("remote");
  }

  default String browserSize(){
    return System.getProperty("selenide.browserSize",
      System.getProperty("selenide.browser-size"));
  }

  default String browserPosition(){
    return System.getProperty("selenide.browserPosition");
  }

  default boolean startMaximized() {
    return Boolean.parseBoolean(System.getProperty("selenide.startMaximized",
    System.getProperty("selenide.start-maximized", "true")));
  }

  default String chromeSwitches() {
    return System.getProperty("selenide.chrome.switches", System.getProperty("chrome.switches"));
  }

  default String pageLoadStrategy(){
    return System.getProperty("selenide.pageLoadStrategy",
      System.getProperty("selenide.page-load-strategy", "normal"));
  }

  default boolean clickViaJs(){
    return Boolean.parseBoolean(System.getProperty("selenide.clickViaJs"));
  }

  default boolean captureJavascriptErrors(){
    return Boolean.parseBoolean(System.getProperty("selenide.captureJavascriptErrors", "true"));
  }

  default boolean screenshots(){
    return Boolean.parseBoolean(System.getProperty("selenide.screenshots", "true"));
  }

  default  boolean savePageSource(){
    return Boolean.parseBoolean(System.getProperty("selenide.savePageSource", "true"));
  }

  default String reportsFolder(){
    return System.getProperty("selenide.reportsFolder",
      System.getProperty("selenide.reports", "build/reports/tests"));
  }

  default boolean headless(){
    return Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));
  }

  default DesiredCapabilities browserCapabilities(){
    return null;
  }

  default String browserBinary(){
    return System.getProperty("selenide.browserBinary", "");
  }

  default boolean driverManagerEnabled(){
    return Boolean.parseBoolean(System.getProperty("selenide.driverManagerEnabled", "true"));
  }

  default Configuration.FileDownloadMode fileDownloadMode(){
    return Configuration.FileDownloadMode.valueOf(System.getProperty("selenide.fileDownloadMode", HTTPGET.name()));
  }

  default boolean dismissModalDialogs(){
    return Boolean.parseBoolean(System.getProperty("selenide.dismissModalDialogs", "false"));
  }

  default boolean fastSetValue() {
    return Boolean.parseBoolean(System.getProperty("selenide.fastSetValue", "false"));
  }

  default boolean versatileSetValue(){
    return Boolean.parseBoolean(System.getProperty("selenide.versatileSetValue", "false"));
  }

  default boolean setValueChangeEvent(){
    return Boolean.parseBoolean(System.getProperty("selenide.setValueChangeEvent", "true"));
  }

  default String reportsUrl(){
    return System.getProperty("selenide.reportsUrl");
  }
}

