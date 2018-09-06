package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public interface Context {
  boolean hasWebDriverStarted();
  Browser getBrowser();
  WebDriver getWebDriver();
  SelenideProxyServer getProxy();
  boolean supportsJavascript();

  <T> T executeJavaScript(String jsCode, Object... arguments);
  String getUserAgent();
  SelenideTargetLocator switchTo();
  Actions actions();
}
