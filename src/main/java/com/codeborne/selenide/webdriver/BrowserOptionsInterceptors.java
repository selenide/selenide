package com.codeborne.selenide.webdriver;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class BrowserOptionsInterceptors {
  public BrowserOptionInterceptor<DesiredCapabilities> remoteDriverCapabilitiesInterceptor = options -> {
  };
  public BrowserOptionInterceptor<ChromeOptions> chromeOptionsInterceptor = options -> {
  };
  public BrowserOptionInterceptor<FirefoxOptions> firefoxOptionsInterceptor = options -> {
  };
  public BrowserOptionInterceptor<InternetExplorerOptions> internetExplorerOptionsInterceptor = options -> {
  };
  public BrowserOptionInterceptor<EdgeOptions> edgeOptionsInterceptor = options -> {
  };
  public BrowserOptionInterceptor<OperaOptions> operaOptionsInterceptor = options -> {
  };
}

