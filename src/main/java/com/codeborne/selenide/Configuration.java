package com.codeborne.selenide;

public class Configuration {
  public static String baseUrl = System.getProperty("selenide.baseUrl");

  public static String getBaseUrl() {
    String result = baseUrl;
    if (result == null) {
      result = Navigation.baseUrl;
      if (result == null) {
        result = "http://localhost:8080";
      }
    }
    return result;
  }
}
