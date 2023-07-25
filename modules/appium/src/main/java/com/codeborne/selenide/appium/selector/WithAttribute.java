package com.codeborne.selenide.appium.selector;

public class WithAttribute extends WithTagAndAttribute {

  public WithAttribute(String attributeName, String attributeValue) {
    super("*", attributeName, attributeValue);
  }

}
