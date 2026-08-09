package com.codeborne.selenide;

import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.EDGE;
import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.Browsers.IE;
import static com.codeborne.selenide.Browsers.INTERNET_EXPLORER;
import static com.codeborne.selenide.Browsers.SAFARI;

public class Browser {
  public final String name;
  public final boolean headless;

  public Browser(String name, boolean headless) {
    this.name = name;
    this.headless = headless;
  }

  public boolean isHeadless() {
    return headless;
  }

  public boolean isChrome() {
    return CHROME.equalsIgnoreCase(name);
  }

  public boolean isChromium() {
    return isChrome() || isEdge();
  }

  public static boolean isChromium(WebDriver driver) {
    return driver instanceof HasCapabilities hasCapabilities &&
      new Browser(hasCapabilities.getCapabilities().getBrowserName(), false).isChromium();
  }

  public boolean isFirefox() {
    return FIREFOX.equalsIgnoreCase(name);
  }

  public boolean isIE() {
    return INTERNET_EXPLORER.equalsIgnoreCase(name) || IE.equalsIgnoreCase(name);
  }

  public boolean isEdge() {
    return EDGE.equalsIgnoreCase(name) || "MicrosoftEdge".equalsIgnoreCase(name);
  }

  public boolean isSafari() {
    return SAFARI.equalsIgnoreCase(name);
  }

  public boolean supportsInsecureCerts() {
    return !isIE() && !isSafari();
  }

  @Override
  public String toString() {
    return headless ?
      "%s{%s:headless}".formatted(getClass().getSimpleName(), name) :
      "%s{%s}".formatted(getClass().getSimpleName(), name);
  }
}
