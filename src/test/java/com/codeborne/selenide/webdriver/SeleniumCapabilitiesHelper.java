package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Capabilities;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

class SeleniumCapabilitiesHelper {
  @SuppressWarnings("unchecked")
  static List<String> getBrowserLaunchArgs(String capability, Capabilities capabilities) {
    // it depends on internal Selenium capabilities structure
    // but there are no ways to do the same by public interfaces
    Map<String, Object> arguments = (Map<String, Object>) capabilities.asMap().get(capability);
    if (arguments == null) {
      return emptyList();
    }
    return (List<String>) arguments.get("args");
  }

  @SuppressWarnings("unchecked")
  static Map<String, Object> getBrowserLaunchPrefs(String capability, Capabilities capabilities) {
    // it depends on internal Selenium capabilities structure
    // but there are no ways to do the same by public interfaces
    Map<String, Object> arguments = (Map<String, Object>) capabilities.asMap().get(capability);
    if (arguments == null) {
      return emptyMap();
    }
    return (Map<String, Object>) arguments.get("prefs");
  }

  @SuppressWarnings("unchecked")
  static List<String> getBrowserLaunchExcludeSwitches(String capability, Capabilities capabilities) {
    // it depends on internal Selenium capabilities structure
    // but there are no ways to do the same by public interfaces
    Map<String, Object> arguments = (Map<String, Object>) capabilities.asMap().get(capability);
    if (arguments == null) {
      return emptyList();
    }
    return asList((String[]) arguments.get("excludeSwitches"));
  }
}
