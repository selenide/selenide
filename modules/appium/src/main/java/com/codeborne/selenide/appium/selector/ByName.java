package com.codeborne.selenide.appium.selector;

public class ByName extends ByTagAndName {

  public ByName(String elementNameAttribute) {
    super("*", elementNameAttribute);
  }
}
