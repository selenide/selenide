package com.codeborne.selenide;

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
}
