package com.codeborne.selenide.appium.selector;

public class WithName extends WithTagAndName {
  public WithName(String elementNameAttribute) {
    super("*", elementNameAttribute);
  }

}
