package com.codeborne.selenide.webdriver;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Capabilities;

class SeleniumCapabilitiesHelper {
  @SuppressWarnings("unchecked")
  static List<String> getBrowserLaunchArgs(String capability, Capabilities capabilities) {
    // it depends on internal Selenium capabilities structure
    // but there are no ways to do the same by public interfaces
    Map<String, Object> arguments = (Map<String, Object>) capabilities.asMap().get(capability);
    if (arguments == null) {
      return Collections.emptyList();
    }
    return (List<String>) arguments.get("args");
  }

  @SuppressWarnings("unchecked")
  static Map<String, Object> getBrowserLaunchPrefs(String capability, Capabilities capabilities) {
    // it depends on internal Selenium capabilities structure
    // but there are no ways to do the same by public interfaces
    Map<String, Object> arguments = (Map<String, Object>) capabilities.asMap().get(capability);
    if (arguments == null) {
      return Collections.emptyMap();
    }
    return (Map<String, Object>) arguments.get("prefs");
  }
}
