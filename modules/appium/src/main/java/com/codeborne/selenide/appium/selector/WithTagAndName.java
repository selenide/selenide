package com.codeborne.selenide.appium.selector;

public class WithTagAndName extends WithTagAndAttribute {

  public WithTagAndName(String tag, String nameAttributeValue) {
    super(tag, "name", nameAttributeValue);
  }

}
