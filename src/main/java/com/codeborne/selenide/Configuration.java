package com.codeborne.selenide;

public class Configuration {
  public static String baseUrl = System.getProperty("selenide.baseUrl", "http://localhost:8080");

  public static long timeout = Long.parseLong(System.getProperty("selenide.timeout", "4000"));
}
