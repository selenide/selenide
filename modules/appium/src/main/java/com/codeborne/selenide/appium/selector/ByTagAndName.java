package com.codeborne.selenide.appium.selector;

public class ByTagAndName extends ByTagAndAttribute {

  public ByTagAndName(String tag, String attributeValue) {
    super(tag, "name", attributeValue);
  }

}
