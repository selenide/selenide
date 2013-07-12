package com.codeborne.selenide.impl;

import org.openqa.selenium.WebDriverException;

public class Cleanup {
  public static Cleanup of = new Cleanup();

  public String webdriverExceptionMessage(WebDriverException webDriverException) {
    return webdriverExceptionMessage(webDriverException.toString());
  }

  protected String webdriverExceptionMessage(String webDriverExceptionInfo) {
    return webDriverExceptionInfo == null || webDriverExceptionInfo.indexOf('\n') == -1 ?
        webDriverExceptionInfo :
        webDriverExceptionInfo
            .substring(0, webDriverExceptionInfo.indexOf('\n'))
            .replaceFirst("(.*)\\(WARNING: The server did not provide any stacktrace.*", "$1")
            .replaceFirst("org\\.openqa\\.selenium\\.(.*)", "$1")
            .trim();
  }
}
