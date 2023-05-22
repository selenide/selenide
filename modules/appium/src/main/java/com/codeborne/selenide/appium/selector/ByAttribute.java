package com.codeborne.selenide.appium.selector;

public class ByAttribute extends ByTagAndAttribute {
  public ByAttribute(String attributeName, String attributeValue) {
    super("*", attributeName, attributeValue);
  }

}
