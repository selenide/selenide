package com.codeborne.selenide;

public record Device(String name, int width, int height, double pixelRatio) {
  public static final Device IPHONE_SE = new Device("iPhone SE", 375, 667, 2.0);
  public static final Device IPHONE_14 = new Device("iPhone 14", 390, 844, 3.0);
  public static final Device PIXEL_7 = new Device("Pixel 7", 412, 915, 2.625);
  public static final Device ONEPLUS_7T = new Device("OnePlus 7T", 480, 194, 2.625);
  public static final Device GALAXY_S21 = new Device("Samsung Galaxy S21", 360, 800, 3.0);
}
